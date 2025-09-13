import diaryMake
import requests
from flask import Flask

app = Flask(__name__)


@app.route("/")
def index():
    return "Hello, Flask"


@app.route("/diaryMake", methods=["POST"])
def diaryMake():
    print("Flask server's diaryMake start!")
    data = requests.json
    diaryText = data.get("diaryText")
    print("post 방식으로 넘어온 다이어리 텍스트" + diaryText)
    diaryMake.getKeywordsByGpt(diaryText)

    print("Flask server's diaryMake end!")
    return diaryText


if __name__ == "__main__":
    app.run(debug=True, port=5000)
