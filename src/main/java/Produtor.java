import org.jgroups.Message;
import org.jgroups.util.Util;

import java.util.concurrent.ThreadLocalRandom;

public class Produtor extends Servidor {
    public static void main(String[] args) throws Exception {
        Produtor produtor = new Produtor();
        produtor.start();
        produtor.trabalha();
    }

    @Override
    protected void trabalha() {
        executorService.submit(() -> {
            while (true) {
                if (fila.cheia()) {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(0, 500));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Produto produto = new Produto();
                    Pedido pedido = new Pedido(Pedido.Tipo.ADICIONA, produto);
                    byte[] buf = Util.streamableToByteBuffer(pedido);
                    Message message = new Message(null, buf);

                    try {
                        channel.send(message);
                        System.out.println("Produzi: " + produto);
                        Thread.sleep(ThreadLocalRandom.current().nextInt(0, 500));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void consome(Pedido pedido) {
        fila.consomeProduto(pedido.getConsumidor(), pedido.getProduto());
    }

    @Override
    protected void nao() {}
}
