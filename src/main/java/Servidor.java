import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Servidor extends ReceiverAdapter {
    protected JChannel channel;
    protected Fila fila = new Fila();
    protected ExecutorService executorService = Executors.newSingleThreadExecutor();

    protected void start() throws Exception {
        channel = new JChannel();
        channel.connect("ProdutorConsumidor");
        channel.setReceiver(this);
        channel.getState(null, 10000);
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("** view: " + view);
    }

    @Override
    public void receive(Message msg) {
        Pedido pedido = msg.getObject();
        switch (pedido.getTipo()) {
            case ADICIONA:
                adiciona(pedido.getProduto());
                break;
            case CONSOME:
                consome(pedido);
                break;
            case FINALIZA:
                finaliza(msg.getSrc());
                break;
            case NAO:
                nao();
                break;
            case SOLICITA:
                solicita(msg);
                break;
        }
    }

    private void adiciona(Produto produto) {
        synchronized (fila) {
            fila.adicionaProduto(produto);
        }
    }

    protected abstract void consome(Pedido pedido);

    private void finaliza(Address address) {
        synchronized (fila) {
            fila.finaliza(address);
        }
    }

    protected abstract void nao();

    private void solicita(Message message) {
        Pedido pedido;
        Message resposta;
        Address solicitante = message.getSrc();

        if (fila.vazia()) {
            pedido = new Pedido(Pedido.Tipo.NAO);
            resposta = new Message(solicitante, pedido);
        } else {
            Produto produto;

            synchronized (fila) {
                produto = fila.consomeProduto(solicitante);
            }

            pedido = new Pedido(Pedido.Tipo.CONSOME, produto, solicitante);
            resposta = new Message(null, pedido);
        }

        try {
            channel.send(resposta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (fila) {
            Util.objectToStream(fila, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        synchronized (fila) {
            fila = (Fila) Util.objectFromStream(new DataInputStream(input));
        }
    }
}
