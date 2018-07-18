import socket

################################
UDP_IP = '127.0.0.1'
UDP_PORT = 5123
################################

sock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sock.bind((UDP_IP,UDP_PORT))

data, addr = sock.recvfrom(64000)
print data
