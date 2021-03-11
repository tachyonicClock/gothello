import getopt, sys 
import neat
import pickle
import gothello
import asyncio
from contendor import Contendor
from os import walk
import itertools
import requests
import json
import random
import os

# Endpoints
gothello.API_ENDPOINT = os.getenv("GOTHELLO_HTTP_ENDPOINT", gothello.API_ENDPOINT)
gothello.WS_ENDPOINT  = os.getenv("GOTHELLO_WS_ENDPOINT", gothello.WS_ENDPOINT)
NETWORKS_PATH         = os.getenv("GOTHELLO_NEURAL_NETWORK_PATH", "networks")

# Neural Networks
NETWORK_NAMES = []
NETWORKS = []
print("Loading Networks:")
for (dirpath, dirnames, filenames) in walk(NETWORKS_PATH):
    NETWORK_NAMES.extend(filenames)
    break
for name in NETWORK_NAMES:
    print("  ", name)
    NETWORKS.append(Contendor(pickle.load(open("networks/" + name, "rb"))))


async def play_game(id):
    net_id = random.randint(0, len(NETWORKS)-1)
    contendor = NETWORKS[net_id]

    gp = await gothello.GothelloPlayer.create(gothello.get_endpoint(id), contendor.run_model, 1)
    print("[{}] playing game with '{}'".format(id, NETWORK_NAMES[net_id]))
    await gp.start()
    if gp.is_winner:
        print("[{}] '{}' won the game".format(id, NETWORK_NAMES[net_id]))
    else:
        print("[{}] '{}' lost the game".format(id, NETWORK_NAMES[net_id]))

async def loop(endpoint):
    print("Polling... ", endpoint)
    while True:
        r = requests.get(endpoint)
        r = r.json()
        if r["messageType"] == "game" and not r["gameFull"]:
            asyncio.get_event_loop().create_task(play_game(r["id"]))

        await asyncio.sleep(1)

asyncio.run(loop(gothello.API_ENDPOINT + "/game/botqueue"))
