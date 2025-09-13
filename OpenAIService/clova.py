import requests
import uuid
import time
import json

api_url = {API_HOST}
secret_key = {SECRET_KEY}
image_file = 'output/presc1.jpg'

request_json = {
    'images': [
        {
            'format': 'jpg',
            'name': 'demo'
        }
    ],
    'requestId': str(uuid.uuid4()),
    'version': 'V2',
    'timestamp': int(round(time.time() * 1000))
}

payload = {'message': json.dumps(request_json).encode('UTF-8')}
files = [
  ('file', open(image_file,'rb'))
]
headers = {
  'X-OCR-SECRET': secret_key
}

response = requests.request("POST", api_url, headers=headers, data = payload, files = files)
# response = requests.request("POST", api_url, data = payload, files = files)


print(response.text.encode('utf8'))