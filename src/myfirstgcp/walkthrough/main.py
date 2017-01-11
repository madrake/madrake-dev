from flask import Flask
app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello World, Now We Are 3!"

if __name__ == "__main__":
    app.debug = True
    app.run(host='0.0.0.0', port=8080)


