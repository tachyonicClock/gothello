import neat
import pickle
import gothello
import asyncio
from train import Contendor

async def main():
    contendor = pickle.load(open("genome-1","rb"))
    gg = gothello.GothelloGame(contendor.run_model, None)
    await gg.play_against_human()

if __name__=='__main__':
    asyncio.run(main())