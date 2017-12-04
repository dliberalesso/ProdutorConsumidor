import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.util.Util;

import java.util.concurrent.ThreadLocalRandom;

public class Consumidor extends Servidor {
    public static void main(String[] args) throws Exception {
        Consumidor consumidor = new Consumidor();
        consumidor.start();
        consumidor.trabalha();
    }

    private void trabalha() {
        Address coord = channel.getView().getCoord();
        Pedido pedido = new Pedido(Pedido.Tipo.SOLICITA);

        try {
            byte[] buf = Util.streamableToByteBuffer(pedido);
            Message message = new Message(coord, buf);
            channel.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void consome(Pedido pedido) {
        Address local = channel.getAddress();
        Address consumidor = pedido.getConsumidor();
        Produto produto = pedido.getProduto();

        synchronized (fila) {
            fila.consomeProduto(consumidor, produto);
        }

        if (local.equals(consumidor)) {
            executorService.submit(() -> {
                produto.run();
                Pedido pedido1 = new Pedido(Pedido.Tipo.FINALIZA, produto);
                try {
                    byte[] buf = Util.streamableToByteBuffer(pedido1);
                    Message message = new Message(null, buf);
                    channel.send(message);
                    Thread.sleep(50);
                    trabalha();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void nao() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 5000));
            trabalha();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
