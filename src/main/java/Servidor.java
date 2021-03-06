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
        trabalha();
    }

    @Override
    public void receive(Message msg) {
        try {
            Pedido pedido = Util.streamableFromByteBuffer(Pedido.class, msg.getRawBuffer(), msg.getOffset(), msg.getLength());
            msg.getObject();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adiciona(Produto produto) {
        fila.adicionaProduto(produto);
    }

    protected abstract void consome(Pedido pedido);

    private void finaliza(Address address) {
        fila.finaliza(address);
    }

    protected abstract void nao();

    private void solicita(Message message) {
        Pedido pedido;
        Message resposta;
        Address solicitante = message.getSrc();

        try {
            if (fila.vazia()) {
                pedido = new Pedido(Pedido.Tipo.NAO);
                byte[] buf = Util.streamableToByteBuffer(pedido);
                resposta = new Message(solicitante, buf);
            } else {
                Produto produto = fila.consomeProduto(solicitante);

                pedido = new Pedido(Pedido.Tipo.CONSOME, produto, solicitante);
                byte[] buf = Util.streamableToByteBuffer(pedido);
                resposta = new Message(null, buf);
            }
            channel.send(resposta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void trabalha();

    @Override
    public void getState(OutputStream output) throws Exception {
        Util.objectToStream(fila, new DataOutputStream(output));
    }

    @Override
    public void setState(InputStream input) throws Exception {
        fila = Util.objectFromStream(new DataInputStream(input));
    }
}
