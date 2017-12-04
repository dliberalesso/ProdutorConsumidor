import org.jgroups.Message;

import java.util.concurrent.ThreadLocalRandom;

public class Produtor extends Servidor {
    public static void main(String[] args) throws Exception {
        Produtor produtor = new Produtor();
        produtor.start();
        produtor.loop();
    }

    private void loop() {
        executorService.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                Pedido pedido = new Pedido(Pedido.Tipo.ADICIONA, new Produto());
                Message message = new Message(null, pedido);

                try {
                    channel.send(message);
                    Thread.sleep(ThreadLocalRandom.current().nextInt(0, 500));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void consome(Pedido pedido) {
        synchronized (fila) {
            fila.consomeProduto(pedido.getConsumidor(), pedido.getProduto());
        }
    }

    @Override
    protected void nao() {}
}
