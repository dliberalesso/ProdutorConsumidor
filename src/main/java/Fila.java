import org.jgroups.Address;
import org.jgroups.util.Streamable;
import org.jgroups.util.Util;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Fila implements Streamable {
    private Queue<Produto> aguardando = new ConcurrentLinkedQueue<>();
    private Map<Address, Produto> consumindo = new ConcurrentHashMap<>();

    public void adicionaProduto(Produto produto) {
        aguardando.add(produto);
    }

    public boolean vazia() {
        return aguardando.isEmpty();
    }

    public boolean cheia() {
        return (aguardando.size() > 100);
    }

    public Produto consomeProduto(Address address) {
        Produto produto = aguardando.poll();
        consumindo.put(address, produto);
        return produto;
    }

    public void consomeProduto(Address address, Produto produto) {
        consumindo.put(address, produto);
    }

    public void finaliza(Address address) {
        consumindo.remove(address);
    }

    public void cancela(Address address) {
        Produto produto = consumindo.remove(address);
        if (produto != null) {
            aguardando.add(produto);
        }
    }

    @Override
    public void writeTo(DataOutput dataOutput) throws Exception {
        Util.objectToStream(aguardando, dataOutput);
        Util.objectToStream(consumindo, dataOutput);
    }

    @Override
    public void readFrom(DataInput dataInput) throws Exception {
        aguardando = Util.objectFromStream(dataInput);
        consumindo = Util.objectFromStream(dataInput);
    }
}
