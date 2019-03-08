

class Client{
    constructor(socket){
        this.socket = socket;

        this.getSocket = () => {
            return this.socket;
        }

        this.getName = () => {
            return this.socket.username;
        }

        this.getChat = () => {
            return this.socket.chatid;
        }
    }
}

module.exports = Client;