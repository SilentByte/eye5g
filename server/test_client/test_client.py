#
# EYE 5G SYSTEM
# Copyright (c) 2021 SilentByte <https://silentbyte.com/>
#

import sys
import asyncio
import websockets

def read_data(filename):
    with open(filename, 'rb') as fp:
        return fp.read()

async def client():
    uri = f'ws://{sys.argv[1]}'
    image_data = read_data(sys.argv[2])
    async with websockets.connect(uri) as ws:
        await ws.send(image_data)
        objects = await ws.recv()
        print(objects)

asyncio.get_event_loop().run_until_complete(client())
