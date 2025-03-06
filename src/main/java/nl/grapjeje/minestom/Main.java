package nl.grapjeje.minestom;

public final class Main {
    public Main instance = this;
    public static Server server;

    public static void main(String[] args) {
        server = new Server();
        server.start();
    }
}
