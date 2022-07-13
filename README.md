# Datcord-Java
Datcord is a java based messenger similar to discord which gives users ability to create servers and channels (or direct chats) and make direct and group calls or screenshare.
It uses Encryption (much like ssl) and all connections with server is encrypted.

# Encryption
First Server chooses a symmetric key , then both server and client generate a pair of private and public key and send it to each other, then server encrypts the symmetric key with client's public key and sends it to client.
the client then decrypts the encrypted key and gets the symmetric key , then all the data transfered between client and server , will be encrypted by the symmetric key that server randomly generated for this client.

# DataStoring
It uses sqlite3 as its database and stores information in them , it stores passwords in hashed form (sha256).

# Notification
It has a notification popup for incomming messages to inform user.

# Call
Either users can join voice channels , or join Screenshare 
