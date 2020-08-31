FROM frolvlad/alpine-python-machinelearning
RUN apk --no-cache add musl-dev linux-headers g++

# Install deps
RUN mkdir -p ./bot/networks
WORKDIR ./bot
COPY ./requirements.txt .
RUN pip3 install -r requirements.txt

COPY ./gothello.py .

COPY ./*.py ./
COPY ./best_networks/* ./networks/

CMD ["python3", "bot_client.py"]
