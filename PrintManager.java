import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrintManager {
    public static void main(String[] args) {
        PrintService printService = new PrintService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Sistema de gestión de impresión ---");
        while (true) {
            System.out.println("Ingrese el nombre del usuario o 'salir' para finalizar:");
            String user = scanner.nextLine();
            if (user.equalsIgnoreCase("salir")) break;

            System.out.println("Ingrese la prioridad (H - Alta, M - Media, L - Baja):");
            char priority = scanner.nextLine().toUpperCase().charAt(0);

            printService.submitJob(user, priority);
        }
        
        System.out.println("Imprimiendo trabajos según su prioridad...");
        printService.processJobs();
    }
}

//Clase que representa un trabajo de impresión
class PrintJob {
    private String user;
    private LocalDateTime timestamp;
    private char priority; // H - Alta, M - Media, L - Baja
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    //Constructor de la clase para asignar los valores del trabajo de impresión
    public PrintJob(String user, LocalDateTime timestamp, char priority) {
        this.user = user;
        this.timestamp = timestamp;
        this.priority = priority;
    }
    
    //Métodos Getters para acceder a los atributos del trabajo de impresión
    public String getUser() { return user; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public char getPriority() { return priority; }

    //Formateo para la impresión de los datos del trabajo
    @Override
    public String toString() {
        return "Prioridad: " + priority + ", Hora: " + timestamp.format(formatter);
    }
}

//Clase que maneja la cola de impresión 
class PrintQueue {
    private List<PrintJob> queue;
    
    //Constructor que inicializa la lista de trabajos de impresión
    public PrintQueue() {
        queue = new ArrayList<>();
    }

    //Agrega un nuevo trabajo a la cola y la ordena según prioridad y tiempo de llegada
    public void addJob(PrintJob job) {
        queue.add(job);
        sortQueue();
    }

    //Procesa el siguiente trabajo en la cola y lo remueve de la lista
    public PrintJob processJob() {
        if (!queue.isEmpty()) {
            return queue.remove(0);
        }
        return null;
    }

    //Verifica si la cola de impresión está vacía
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    //Se usa para obtener el valor de la prioridad para ordenar las impresiones 
    private int getPriorityValue(char priority) {
        return switch (priority) {
            case 'H' -> 3;
            case 'M' -> 2;
            case 'L' -> 1;
            default -> 0;
        };
    }

    //Ordena la cola de impresión según prioridad y, en caso de igualdad, por hora de llegada
    private void sortQueue() {
        queue.sort((j1, j2) -> {
            int priorityOrder = getPriorityValue(j2.getPriority()) - getPriorityValue(j1.getPriority());
            if (priorityOrder != 0) {
                return priorityOrder; 
            }
            return j1.getTimestamp().compareTo(j2.getTimestamp()); // En caso de misma prioridad, se compara por hora de envío
        });
    }
}

//Clase que simula el servicio de impresión
class PrintService {
    private PrintQueue printQueue;

    //Constructor que inicializa la cola de impresión
    public PrintService() {
        printQueue = new PrintQueue();
    }

    //Método para enviar un nuevo trabajo a la cola de impresión con validación de prioridad
    public void submitJob(String user, char priority) {
        if (priority != 'L' && priority != 'M' && priority != 'H') {
            priority = 'M'; //Prioridad por defecto
        }
        printQueue.addJob(new PrintJob(user, LocalDateTime.now(), priority));
    }

    //Método que procesa todos los trabajos en la cola de impresión en orden de prioridad
    public void processJobs() {
        while (!printQueue.isEmpty()) {
            PrintJob job = printQueue.processJob();
            System.out.println("Imprimiendo trabajo de " + job.getUser() + ": " +  job);
        }
    }
}
