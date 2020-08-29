import gothello
import itertools
import random
import asyncio
from concurrent.futures import ThreadPoolExecutor


def gothello_game_thread(gg):
    loop = asyncio.new_event_loop()
    return loop.run_until_complete(gg.play_game())

def tournament():

    n = 50
    contendors = [gothello.random_model] * n
    scores = [0] * n

    combinations = list(itertools.combinations(range(len(contendors)), 2))
    num_combinations = len(combinations)
    sample_ratio = 1
    sample_size = int(num_combinations * sample_ratio)

    print("Conducting Tournament")
    print("Sample of matches: {:0.2f}% {:d}".format(sample_ratio * 100, sample_size))
    print("Number possible matches:", num_combinations)

    random.shuffle(combinations)
    with ThreadPoolExecutor(max_workers=10) as executor:
        winners = []
        for i in range(sample_size):
            a, b = combinations[i]
            gg = gothello.GothelloGame(contendors[a], contendors[b])

            winners.append(executor.submit(gothello_game_thread, gg))

    for i in range(sample_size):
        a, b = combinations[i]
        winner = winners[i].result()
        if winner == "A":
            scores[a] += 1
        elif winner == "B":
            scores[b] += 1
        else:
            scores[a] += 0.5
            scores[b] += 0.5

    for score in scores:
        print("Score: ", score/sample_size)

# def eval_genome():

tournament()
# asyncio.run(tournament())