package jm.edu.utech.helpdesk.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import jm.edu.utech.helpdesk.model.Ticket;
import jm.edu.utech.helpdesk.util.AlertUtil;

//keeps the technician's table up to date without freezing the screen

public class TicketRefreshService implements Runnable {

    private static final Logger logger = LogManager.getLogger(TicketRefreshService.class);

    private final TableView<Ticket> table;
    private final String username;
    private final int seconds;

    private Thread thread;
    private List<String> lastSnapshot;   //what the table showed last time
    private boolean failureShown;        //the error alert only shows once

    public TicketRefreshService(TableView<Ticket> table, String username, int seconds) {
        this.table = table;
        this.username = username;
        this.seconds = seconds;
        this.lastSnapshot = snapshot(table.getItems());
    }

    //starts the background thread
    public void start() {
        thread = new Thread(this, "ticket-refresh");
        thread.setDaemon(true);   //so this thread never keeps the program open after the window closes
        thread.start();
        logger.info("Ticket refresh started");
    }

    //stops the background thread, interrupt() wakes it up if it is sleeping
    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
        logger.info("Ticket refresh stopped");
    }

    //runs on the background thread
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(seconds * 1000L);

                //REST call, on this background thread so the screen never waits for it
                List<Ticket> latest = TicketApi.getTicketsByTechnician(username);
                failureShown = false;
                Platform.runLater(() -> applyIfChanged(latest));

            } catch (InterruptedException e) {
                break;   //stop() was called
            } catch (RuntimeException e) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                logger.error("Background ticket refresh failed", e);
                if (!failureShown) {
                    failureShown = true;
                    Platform.runLater(() -> AlertUtil.showError("Unable to Connect to Server", "Tickets could not be refreshed. Will keep trying in the background."));
                }
            }
        }
    }

    //runs on the JavaFX thread, only changes the table if the tickets are different
    private void applyIfChanged(List<Ticket> latest) {
        List<String> newSnapshot = snapshot(latest);
        if (newSnapshot.equals(lastSnapshot)) {
            return;
        }
        lastSnapshot = newSnapshot;

        //remember the selected ticket so it stays selected after the refresh
        Ticket selected = table.getSelectionModel().getSelectedItem();
        Long selectedId = selected == null ? null : selected.getId();

        table.setItems(FXCollections.observableArrayList(latest));

        if (selectedId != null) {
            for (Ticket ticket : table.getItems()) {
                if (selectedId.equals(ticket.getId())) {
                    table.getSelectionModel().select(ticket);
                    break;
                }
            }
        }
        logger.info("Technician dashboard refreshed ({} tickets)", latest.size());
    }

    //one line of text per ticket, used to check if anything changed
    private static List<String> snapshot(List<Ticket> tickets) {
        List<String> lines = new ArrayList<>();
        for (Ticket t : tickets) {
            lines.add(t.getId() + "|" + t.getStatus() + "|" + t.getPriority() + "|"
                    + t.getAssignedTechnician() + "|" + t.getResolutionNotes());
        }
        return lines;
    }
}
