from flask import Flask, request, Response, jsonify


app = Flask(__name__)

# 初始状态码
code = 200

@app.route('/', methods=['GET', 'POST'])
def handle_request():
    global code

    if request.method == 'GET':
        # 如果是GET请求，直接返回当前状态码
        return Response(status=code)

    elif request.method == 'POST':
        # 如果是POST请求，根据请求的JSON数据修改状态码
        try:
            data = request.get_json()
            print(f"Received POST data: {data}")  # 打印接收到的POST数据
            new_code = data.get('code')
            if new_code is not None and isinstance(new_code, int):
                code = new_code
                print("New code is "+str(code))  # 注意这里要将code转换为字符串才能打印
                return jsonify({'message': 'Code updated successfully', 'new_code': code})
            else:
                return jsonify({'error': 'Invalid data format'})

        except Exception as e:
            return jsonify({'error': f'Error processing request: {str(e)}'})

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000)
