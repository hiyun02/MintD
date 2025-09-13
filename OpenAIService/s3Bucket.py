import boto3
from botocore.exceptions import ClientError

ACCESS_KEY_ID = {ACCESS_KEY_ID}
SECRET_ACCESS_KEY = {SECRET_ACCESS_KEY}
BUCKET_NAME = {BUCKET_NAME}


# s3 버킷 생성하는 함수
def create_s3_bucket():
    print("Creating a bucket... " + BUCKET_NAME)

    s3 = boto3.client(
        's3',  # 사용할 서비스 이름, ec2이면 'ec2', s3이면 's3', dynamodb이면 'dynamodb'
        aws_access_key_id=ACCESS_KEY_ID,  # 액세스 ID
        aws_secret_access_key=SECRET_ACCESS_KEY
    )  # 비밀 엑세스 키

    print("stay here")
    try:
        response = s3.create_bucket(
            Bucket=BUCKET_NAME,
            CreateBucketConfiguration={
                'LocationConstraint': 'ap-northeast-2'  # Seoul  # us-east-1을 제외한 지역은 LocationConstraint 명시해야 함.
            }
        )
        return response

    except ClientError as e:
        if e.response['Error']['Code'] == 'BucketAlreadyOwnedByYou':
            print("Bucket already exists. skipping..")
        else:
            print("Unknown error, exit..")
            print(e)


def uploadFileToS3Bucket(fileName):
    s3_client = boto3.client(
        's3',
        aws_access_key_id=ACCESS_KEY_ID,
        aws_secret_access_key=SECRET_ACCESS_KEY
    )

    # 로컬파일경로 + 파일명 + 파일종류, 버킷명, s3버킷의 원하는경로 + 파일명 + 파일종류
    response = s3_client.upload_file(
        'output/' + fileName + '.jpg', BUCKET_NAME, fileName + '.jpg'
    )

    print(response)