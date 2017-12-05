import org.jgroups.Address;
import org.jgroups.util.Streamable;
import org.jgroups.util.Util;

import java.io.DataInput;
import java.io.DataOutput;

public class Pedido implements Streamable {
    enum Tipo {ADICIONA, CONSOME, FINALIZA, NAO, SOLICITA}

    private Tipo tipo;
    private Produto produto;
    private Address consumidor;

    public Pedido() {
    }

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

    @Override
    public void writeTo(DataOutput dataOutput) throws Exception {
        dataOutput.writeInt(tipo.ordinal());
        Util.objectToStream(produto, dataOutput);
        Util.writeAddress(consumidor, dataOutput);
    }

    @Override
    public void readFrom(DataInput dataInput) throws Exception {
        int tmp = dataInput.readInt();
        switch (tmp) {
            case 0:
                tipo = Tipo.ADICIONA;
                break;
            case 1:
                tipo = Tipo.CONSOME;
                break;
            case 2:
                tipo = Tipo.FINALIZA;
                break;
            case 3:
                tipo = Tipo.NAO;
            case 4:
                tipo = Tipo.SOLICITA;
                break;
        }

        produto = Util.objectFromStream(dataInput);
        consumidor = Util.readAddress(dataInput);
    }
}