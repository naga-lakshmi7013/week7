class SharedBuffer {
    private final int[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;

    SharedBuffer(int size) {
        buffer = new int[size];
    }

    synchronized void produce(int item) throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }

        buffer[in] = item;
        in = (in + 1) % buffer.length;
        count++;

        System.out.println("Produced: " + item);

        notifyAll();
    }

    synchronized int consume() throws InterruptedException {
        while (count == 0) {
            wait();
        }

        int item = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        System.out.println("Consumed: " + item);

        notifyAll();
        return item;
    }
}

class Producer extends Thread {
    private final SharedBuffer buffer;

    Producer(SharedBuffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                buffer.produce(i);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer extends Thread {
    private final SharedBuffer buffer;

    Consumer(SharedBuffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                buffer.consume();
                Thread.sleep(800);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class ProducerConsumer {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(5);

        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        producer.start();
        consumer.start();
    }
}
