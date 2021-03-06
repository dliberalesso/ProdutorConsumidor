import org.jgroups.util.Streamable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Produto implements Serializable, Streamable, Runnable {
    private static final AtomicInteger contador = new AtomicInteger(0);
    private int id;
    private int milisegundos;

    public Produto() {
        this.id = contador.incrementAndGet();
        this.milisegundos = ThreadLocalRandom.current().nextInt(250, 5000);
    }

    public int getId() {
        return id;
    }

    public int getMilisegundos() {
        return milisegundos;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.milisegundos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Consumido: " + this);
    }

    @Override
    public String toString() {
        return "Produto{" + "id=" + id + ", milisegundos=" + milisegundos + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return getId() == produto.getId() && getMilisegundos() == produto.getMilisegundos();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMilisegundos());
    }

    @Override
    public void writeTo(DataOutput dataOutput) throws Exception {
        dataOutput.writeInt(id);
        dataOutput.writeInt(milisegundos);
    }

    @Override
    public void readFrom(DataInput dataInput) throws Exception {
        id = dataInput.readInt();
        milisegundos = dataInput.readInt();
    }
}
