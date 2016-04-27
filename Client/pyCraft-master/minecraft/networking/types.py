"""Contains definitions for minecraft's different data types
Each type has a method which is used to read and write it.
These definitions and methods are used by the packet definitions
"""
import struct
import nbt


class Type(object):
    @staticmethod
    def read(file_object):
        raise NotImplementedError("Base data type not serializable")

    @staticmethod
    def send(value, socket):
        raise NotImplementedError("Base data type not serializable")


# =========================================================


class UUID(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>LL', file_object.read(8))

    @staticmethod
    def send(values, socket):
        socket.send(struct.pack('>LL', values[0], values[1]))


class Slot(Type):
    @staticmethod
    def read(file_object):
        ret = []
        block_id = struct.unpack('>h', file_object.read(2))[0]
        ret.append(block_id)
        if block_id != -1:
            item_count = struct.unpack('>B', file_object.read(1))[0]
            item_damage = struct.unpack('>h', file_object.read(2))[0]
            ret.append(item_count)
            ret.append(item_damage)
            nbtfile = nbt.NBTFILE(fileobj=file_object)
            ret.append(nbtfile)
        return ret

    @staticmethod
    def send(values, socket):
        if len(values) == 1:
            socket.send(struct.pack('>h', values[0]))
        elif len(values) == 4:
            socket.send(struct.pack('>hBh', values[0], values[1], values[2]))
            values[3].write_file(buffer=socket.makefile())  # TODO this is probably wrong


class SlotArray(Type):
    @staticmethod
    def read(file_object):
        ret = []
        count = struct.unpack('>h', file_object.read(2))[0]
        ret.append(count)
        for x in range(0, count):
            ret.append(Slot.read(file_object))
        return ret

    @staticmethod
    def send(values, socket):
        socket.send(struct.pack('>h', values[0]))
        for x in range(1, values[0]+1):
            Slot.send(values[x], socket)  # TODO this is probably wrong


class TripleByteArray(Type):
    @staticmethod
    def read(file_object):
        ret = []
        count = Integer.read(file_object)
        ret.append(count)
        for x in range(0, count):
            first = Byte.read(file_object)
            second = Byte.read(file_object)
            third = Byte.read(file_object)
            ret.append([first, second, third])
        return ret

    @staticmethod
    def send(values, socket):
        socket.send(struct.pack('>i', values[0]))
        for x in range(1, values[0]+1):
            socket.send(struct.pack('>BBB', values[x][0], values[x][1], values[x][2]))  # TODO this is probably wrong


class RecordArray(Type):
    @staticmethod
    def read(file_object):
        ret = []
        count = VarInt.read(file_object)
        ret.append(count)
        for x in range(0, count):
            horizontal_position = UnsignedByte.read(file_object)
            y_coord = UnsignedByte.read(file_object)
            block_id = VarInt.read(file_object)
            ret.append([horizontal_position, y_coord, block_id])
        return ret

    @staticmethod
    def send(values, socket):
        VarInt.send(values[0], socket)
        for x in range(1, values[0]+1):
            UnsignedByte.send(values[x][0], socket)
            UnsignedByte.send(values[x][1], socket)
            VarInt.send(values[x][2], socket)


class NBT(Type):
    @staticmethod
    def read(file_object):
        return nbt.NBTFILE(fileobj=file_object)

    @staticmethod
    def send(value, socket):
        value.write_file(buffer=socket.makefile())


class Boolean(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('?', file_object.read(1))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('?', value))


class UnsignedByte(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>B', file_object.read(1))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('>B', value))


class Byte(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>b', file_object.read(1))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('>b', value))


class Short(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>h', file_object.read(2))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('>h', value))


class UnsignedShort(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>H', file_object.read(2))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('>H', value))


class Integer(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>i', file_object.read(4))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('>i', value))


class VarInt(Type):
    @staticmethod
    def read_socket(socket):
        number = 0
        for i in range(5):
            byte = socket.recv(1)
            if byte == "" or len(byte) == 0:
                raise RuntimeError("Socket disconnected")
            byte = ord(byte)
            number |= (byte & 0x7F) << 7 * i
            if not byte & 0x80:
                break
        return number

    @staticmethod
    def read(file_object):
        number = 0
        for i in range(5):
            byte = ord(file_object.read(1))
            number |= (byte & 0x7F) << 7 * i
            if not byte & 0x80:
                break
        return number

    @staticmethod
    def send(value, socket):
        out = bytes()
        while True:
            byte = value & 0x7F
            value >>= 7
            out += struct.pack("B", byte | (0x80 if value > 0 else 0))
            if value == 0:
                break
        socket.send(out)

    @staticmethod
    def size(value):
        for max_value, size in VARINT_SIZE_TABLE.items():
            if value < max_value:
                return size

# Maps (maximum integer value -> size of VarInt in bytes)
VARINT_SIZE_TABLE = {
    2 ** 7: 1,
    2 ** 14: 2,
    2 ** 21: 3,
    2 ** 28: 4,
    2 ** 35: 5,
    2 ** 42: 6,
    2 ** 49: 7,
    2 ** 56: 8,
    2 ** 63: 9,
    2 ** 70: 10,
    2 ** 77: 11,
    2 ** 84: 12
}


class Long(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>q', file_object.read(8))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('>q', value))


class Float(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>f', file_object.read(4))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('>f', value))


class Double(Type):
    @staticmethod
    def read(file_object):
        return struct.unpack('>d', file_object.read(8))[0]

    @staticmethod
    def send(value, socket):
        socket.send(struct.pack('>d', value))


class ShortPrefixedByteArray(Type):
    @staticmethod
    def read(file_object):
        length = Short.read(file_object)
        return struct.unpack(str(length) + "s", file_object.read(length))[0]

    @staticmethod
    def send(value, socket):
        Short.send(len(value), socket)
        socket.send(value)


class VarIntPrefixedByteArray(Type):
    @staticmethod
    def read(file_object):
        length = VarInt.read(file_object)
        return struct.unpack(str(length) + "s", file_object.read(length))[0]

    @staticmethod
    def send(value, socket):
        VarInt.send(len(value), socket)
        socket.send(struct.pack(str(len(value)) + "s", value))


class String(Type):
    @staticmethod
    def read(file_object):
        length = VarInt.read(file_object)
        return file_object.read(length).decode("utf-8")

    @staticmethod
    def send(value, socket):
        value = value.encode('utf-8')
        VarInt.send(len(value), socket)
        socket.send(value)
