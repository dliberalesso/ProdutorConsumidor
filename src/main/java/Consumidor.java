import org.jgroups.Address;
import org.jgroups.Message;

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
        Message message = new Message(coord, pedido);

        try {
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
                Message message = new Message(null, pedido1);

                try {
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
