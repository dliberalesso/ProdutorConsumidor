import org.jgroups.Address;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Fila implements Serializable {
    private Queue<Produto> aguardando = new ConcurrentLinkedQueue<>();
    private Map<Address, Produto> consumindo = new ConcurrentHashMap<>();

    public void adicionaProduto(Produto produto) {
        aguardando.add(produto);
    }

    public boolean vazia() {
        return aguardando.isEmpty();
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
}
