import getpass
import socket
import sys
import time
from optparse import OptionParser

from minecraft import authentication
from minecraft.exceptions import YggdrasilError
from minecraft.networking.connection import Connection
from minecraft.networking.packets import ChatMessageClientboundPacket, ChatPacket
from minecraft.compat import input


def get_options():
    parser = OptionParser()

    parser.add_option("-u", "--username", dest="username", default=None,
                      help="username to log in with")

    parser.add_option("-p", "--password", dest="password", default=None,
                      help="password to log in with")

    parser.add_option("-s", "--server", dest="server", default=None,
                      help="server to connect to")

    (options, args) = parser.parse_args()

    if not options.username:
        options.username = input("Enter your username: ")

    if not options.password:
        options.password = getpass.getpass("Enter your password: ")

    if not options.server:
        options.server = input("Please enter server address"
                               " (including port): ")
    # Try to split out port and address
    if ':' in options.server:
        server = options.server.split(":")
        options.address = server[0]
        options.port = int(server[1])
    else:
        options.address = options.server
        options.port = 25565

    return options


def print_chat(chat_packet):
    print("Data: " + chat_packet.json_data)


def send_message(data, addr, connection):
    data = data.replace(" ", "\ ")
    data = data.replace("\n", "\\ n")
    data = data.replace("\r", "\\ r")
    packet = ChatPacket()
    packet.message = "/k " + addr
    connection.write_packet(packet)

    remainder = len(data) % 90
    num_packets = len(data) / 90

    for x in range(0, num_packets):
        packetj = ChatPacket()
        packetj.message = "/j " + data[x*90:(x+1)*90]
        print packetj.message
        connection.write_packet(packetj)
    if remainder > 0:
        packetj = ChatPacket()
        packetj.message = "/j " + data[num_packets*90:]
        print packetj.message
        connection.write_packet(packetj)

    packetl = ChatPacket()
    packetl.message = "/l"
    connection.write_packet(packetl)


def main():
    options = get_options()

    auth_token = authentication.AuthenticationToken()
    try:
        auth_token.authenticate(options.username, options.password)
    except YggdrasilError as e:
        print(e)
        sys.exit()
    connection = Connection(options.address, options.port, auth_token)
    connection.connect()

    print("Logged in as " + auth_token.username)

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(("", 9001))
    s.listen(5)

    connection.register_packet_listener(print_chat, ChatMessageClientboundPacket)

    #send_message("GET http://www.google.com/ HTTP/1.1\r\nhost: www.google.com\r\n\r\n", "address", connection)  #GET http://www.uga.edu/ HTTP/1.1\r\nHost: www.uga.edu\r\n

    #time.sleep(100)

    #sys.exit()
    while True:
        try:
            (conn, addr) = s.accept()
            print("got packet")
            data = conn.recv(8192)
            send_message(data, addr, connection)
        except KeyboardInterrupt:
            sys.exit()
    s.close()


if __name__ == "__main__":
    main()

