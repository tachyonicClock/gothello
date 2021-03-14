import neat
import pickle
import gothello
import asyncio
from progress.bar import Bar
from contendor import Contendor
from os import walk
import itertools
import click

'''
233-misty-wind.pkl             15.38%
damp-silence.pkl               19.23%
229-nameless-sun.pkl           19.23%
delicate-lake.pkl              23.08%
266-throbbing-frost.pkl        23.08%
315-wispy-butterfly.pkl        23.08%
310-long-frog.pkl              26.92%
291-still-glitter.pkl          26.92%
251-muddy-sun.pkl              30.77%
305-broken-sky.pkl             38.46%
320-late-water.pkl             42.31%
274-bold-moon.pkl              42.31%
damp-frost.pkl                 42.31%
hidden-paper.pkl               46.15%
286-dawn-resonance.pkl         50.00%
261-lively-resonance.pkl       57.69%
bold-sun.pkl                   61.54%
296-purple-glade.pkl           61.54%
246-black-resonance.pkl        65.38%
238-autumn-silence.pkl         69.23%
276-patient-smoke.pkl          69.23%
301-long-meadow.pkl            73.08%
256-little-wind.pkl            76.92%
281-blue-field.pkl             76.92%
241-winter-glade.pkl           80.77%
271-polished-rain.pkl          88.46%
'''

async def dual_models(name_a, name_b):
    a = Contendor.from_net(pickle.load(open(name_a, "rb")))
    b = Contendor.from_net(pickle.load(open(name_b, "rb")))

    gg = gothello.GothelloGame(a, b)
    win = await gg.play_game()

    if a.won == 1:
        print(name_a, "is the winner over", name_b)
    elif b.won == 1:
        print(name_b, "is the winner over", name_a)
    elif a.drew == 1:
        print(name_a, name_b, "drew")
    elif a.expired == 1:
        print(name_a, name_b, "reached turn limit")

async def network_tournament():
    f = []
    scores = {}
    for (dirpath, dirnames, filenames) in walk("best_networks"):
        f.extend(filenames)
        break
    for name in f:
        scores[name] = 0.0
    num_contendors = len(f)
    matches = list(itertools.combinations(f, 2))
    num_matches = len(matches)

    print("Running", num_matches, "games")
    for a_model, b_model in matches:
        win = await dual_models(a_model, b_model)
        if win == "A":
            scores[a_model] += 1
        elif win == "B":
            scores[b_model] += 1
        elif win == "AB":
            scores[a_model] += 0.5
            scores[b_model] += 0.5
    
    scores = sorted(scores.items(), key=lambda x: x[1])
    for name, score in scores:
        print("{:30} {:2.2f}%".format(name, score/num_contendors * 100))


@click.command()
@click.option("--model-a", "-a",
              help='pkl file containing a trained neural network')
@click.option("--model-b", "-b",
              help='pkl file containing a trained neural network')
def dual(model_a, model_b):
    """Resume a previous training session using a checkpoint"""
    asyncio.run(dual_models(model_a, model_b))

@click.command()
@click.option("--model", "-m",
              help='pkl file containing a trained neural network')
def vs_human(model):
    a = Contendor.from_net(pickle.load(open(model, "rb")))
    gg = gothello.GothelloGame(a, None)
    print("Game Started Against '{}' /game/{}".format(model, gg.id))
    asyncio.run(gg.play_against_human())

@click.group()
def cli():
    pass

if __name__ == "__main__":
    cli.add_command(dual)
    cli.add_command(vs_human)
    cli()
