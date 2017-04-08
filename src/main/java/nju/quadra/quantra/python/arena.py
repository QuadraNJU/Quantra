import json
import time
from threading import Thread

import engine


class EngineThread(Thread):
    def __init__(self, args, id):
        Thread.__init__(self)
        self.args = args
        self.id = id
        self.result = None

    def run(self):
        self.result = engine.run(self.args, self.id)


if __name__ == '__main__':
    # workaround for a bug in strptime - ref: http://bugs.python.org/issue7980
    time.strptime('', '')

    argss = json.loads(raw_input())
    engine.init()
    threads = []
    results = []

    for i in range(0, len(argss)):
        args = argss[i]
        thread = EngineThread(args, i)
        threads.append(thread)
        thread.start()

    for i in range(0, len(argss)):
        threads[i].join()
