import org.jgroups.Address;

import java.io.Serializable;

public class Pedido implements Serializable {
    static enum Tipo {ADICIONA, CONSOME, FINALIZA, NAO, SOLICITA};

    private Tipo tipo;
    private Produto produto;
    private Address consumidor;

    public Pedido(Tipo tipo) {
        this.tipo = tipo;
        this.produto = null;
        this.consumidor = null;
    }

    public Pedido(Tipo tipo, Produto produto) {
        this.tipo = tipo;
        this.produto = produto;
        this.consumidor = null;
    }

    public Pedido(Tipo tipo, Produto produto, Address consumidor) {
        this.tipo = tipo;
        this.produto = produto;
        this.consumidor = consumidor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Produto getProduto() {
        return produto;
    }

    public Address getConsumidor() {
        return consumidor;
    }
}