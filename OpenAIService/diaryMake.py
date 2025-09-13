import openai
from base64 import b64decode
from PIL import Image
from io import BytesIO
import s3Bucket

# openai의 api-key
API_KEY = {API_KEY}


def getImageByDall_e(prompt, fileName):
    openai.api_key = API_KEY

    response = openai.Image.create(
        prompt=prompt, n=1, size="256x256", response_format="b64_json"
    )
    print("response : ", response)
    img_b64 = b64decode(response["data"][0]["b64_json"])

    imgPath = "output\\" + fileName + ".jpg"
    Image.open(BytesIO(img_b64)).save(imgPath)

    return imgPath


def getKeywordsByGpt(text):
    # ChatGPT API 인증 설정
    openai.api_key = API_KEY

    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role": "system", "content": "/set_context"},
            {"role": "user", "content": text}
        ]
    )

    keywords = response.choices[0].message.content
    return keywords


if __name__ == "__main__":
    # 예시 텍스트
    text = "I went on a trip with my cat. Because I like cats. Traveling with cats is fun. Because cats are very cute. I put my cat on my bike and left. Both my cat and I love riding bike."
    print(text)
    # 키워드 추출
    keywords = getKeywordsByGpt(text)
    print(keywords)

    keywords = keywords.replace('Context: ', '')
    keywords = keywords.replace('Context set: ', '')
    keywords = keywords.replace('Context set!: ', '')
    print(keywords)

    resultPath1 = getImageByDall_e(text, "testImg")
    print(resultPath1)

    # resultPath2 = getImageByDall_e(keywords, 5)
    # print(resultPath2)
    result = s3Bucket.uploadFileToS3Bucket("testImg")
    print(result)