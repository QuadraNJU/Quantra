import json

import engine
from threading import Thread


class EngineThread(Thread):
    def __init__(self, args):
        Thread.__init__(self)
        self.args = args
        self.result = None

    def run(self):
        self.result = engine.run(self.args)


if __name__ == '__main__':
    argss = json.loads(raw_input())
    engine.init()
    threads = []
    lis = []
    f = open('../result.json', 'wt')
    f.close()

    for i in range(0, len(argss)):
        args = argss[i]
        thread = EngineThread(args)
        threads.append(thread)
        thread.start()

    for i in range(0, len(argss)):
        threads[i].join()

    for i in range(0, len(argss)):
        lis.append(threads[i].result)

    f = open('../result.json', 'a')
    f.write(json.dumps(lis))
    f.close()
