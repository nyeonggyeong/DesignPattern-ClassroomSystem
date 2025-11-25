/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package management.iterator;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author suk22
 */
public class ReservationIterator implements Iterator<Reservation> {

    private BufferedReader reader;
    private String nextLine;

    public ReservationIterator(String filePath) {
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)
            );
            nextLine = reader.readLine();
        } catch (Exception e) {
            nextLine = null;
        }
    }

    @Override
    public boolean hasNext() {
        return nextLine != null;
    }

    @Override
    public Reservation next() {
        String current = nextLine;

        try {
            nextLine = reader.readLine();
            if (nextLine == null) {
                reader.close();
            }
        } catch (Exception ignored) {
        }

        return new Reservation(current);
    }
}
