package com.goriant.game.server.visualizer;

import com.goriant.game.server.client.WebSocketClient;
import com.goriant.game.server.config.adapter.KeyListenerAdapter;
import com.goriant.game.server.config.adapter.MouseListenerAdapter;
import com.goriant.game.server.enums.MoveDirection;
import com.goriant.game.server.service.AOISystem;
import com.goriant.game.server.service.aoi.GridCell;
import com.goriant.game.server.model.Player;
import com.goriant.game.server.random.MyRandomizer;
import com.goriant.game.server.service.PlayerService;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.goriant.game.server.constants.Constants.RADIUS;
import static com.goriant.game.server.converter.GridHeightConverter.unityToAoiY;

@Slf4j
public class AOIVisualizer extends JPanel {

    private final int gridSize;
    private final int cellSize;
    private final transient ClientJoinHandler clientJoinHandler;
    private final transient ClientMoveHandler clientMoveHandler;
    private final transient AOISystem aoiSystem;
    private final transient Player mainPlayer;
    private final transient List<Player> players;

    @Inject
    public AOIVisualizer(AOISystem aoiSystem, Player mainPlayer, String serverHost) {
        this.gridSize = aoiSystem.getGridSize();
        this.cellSize = aoiSystem.getCellSize();
        this.aoiSystem = aoiSystem;
        this.mainPlayer = mainPlayer;
        this.players = aoiSystem.getPlayers();

        WebSocketClient client = WebSocketClient.from(serverHost);
        this.clientJoinHandler = new ClientJoinHandler(client, new ConcurrentLinkedQueue<>());
        this.clientMoveHandler = new ClientMoveHandler(client, new ConcurrentLinkedQueue<>());
        new Thread(clientJoinHandler).start();
        new Thread(clientMoveHandler).start();

        aoiSystem.addPlayer(mainPlayer);
        clientJoinHandler.addPlayer(mainPlayer);

        setPreferredSize(new Dimension(gridSize, gridSize));
        Timer timer = new Timer(2000, e -> {
            updatePlayerPositions();
            checkMainPlayerCollisions();
            repaint();
        });
        timer.start();

        // Key listener for main player movement
        addKeyListener(KeyListenerAdapter.adapter(this::controlMainPlayer));
        addMouseListener(MouseListenerAdapter.adapter(this::handleMouseClick));
        setFocusable(true);
    }

    public void startGui() {
        JFrame frame = new JFrame("AOI Visualizer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Add example players

//        Player p1 = PlayerService.nextPlayer();
//        Player p2 = PlayerService.nextPlayer();
//        p1.setSpeed(50);
//        p2.setSpeed(100);
//        p1.setPosition(Player.Position.from(p1.getPosition().getX(), unityToAoiY(p1.getPosition().getY())));
//        p2.setPosition(Player.Position.from(p2.getPosition().getX(), unityToAoiY(p2.getPosition().getY())));
//        aoiSystem.addPlayer(p1);
//        aoiSystem.addPlayer(p2);
//
//        clientJoinHandler.addPlayer(p1);
//        clientJoinHandler.addPlayer(p2);

        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    private void checkMainPlayerCollisions() {
        if (mainPlayer != null) {
            for (Player player : players) {
                if (player != mainPlayer) {
                    double distance = Math.sqrt(
                            Math.pow(player.getPosition().getX() - mainPlayer.getPosition().getX(), 2) +
                                    Math.pow(player.getPosition().getY() - mainPlayer.getPosition().getY(), 2)
                    );
                    if (distance < RADIUS)
                        log.info("Collision detected between main player {} vs player {}", mainPlayer.getName(), player.getName());
                }
            }
        }
    }

    private void controlMainPlayer(KeyEvent e) {
        if (mainPlayer != null) {
            Player p = mainPlayer;
            int key = e.getKeyCode();
            float moveAmount = 0.1f;

            switch (key) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    p.setDirection(MoveDirection.UP.degree());
                    move(p, p.getPosition().getX(), p.getPosition().getY() - moveAmount);
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    p.setDirection(MoveDirection.DOWN.degree());
                    move(p, p.getPosition().getX(), p.getPosition().getY() + moveAmount);
                }
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                    p.setDirection(MoveDirection.LEFT.degree());
                    move(p, p.getPosition().getX() - moveAmount, p.getPosition().getY());
                }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                    p.setDirection(MoveDirection.RIGHT.degree());
                    move(p, p.getPosition().getX() + moveAmount, p.getPosition().getY());
                }
                default -> log.info("Unknown key code {}", key);
            }
            p.ensurePlayerWithinBounds(gridSize);
            repaint();
        }
    }

    private void move(Player p, float x, float y) {
        p.setPosition(Player.Position.from(x, y));
        clientMoveHandler.move(p.moveMsg(p.getPosition().getY()));
    }

    private void updatePlayerPositions() {
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Integer playerId : cell.getPlayers()) {
                    Player player = aoiSystem.getPlayerById(playerId);
                    Objects.requireNonNull(player, "player id " + playerId + " is null when updatePlayerPositions");
                    if (player.isUnity() || player.getId() == 123456) {
                        continue;
                    }
                    movePlayer(player);
                    handlePlayerCollisions(player);
                }
            }
        }
        repaint(); // Redraw the grid with updated player positions
    }

    private void movePlayer(Player p) {
        p.moveGui(0.1f);

        if (p.isPlayerOutOfBounds(gridSize))
            p.setDirection(MyRandomizer.random().nextFloat() * 360);
        clientMoveHandler.move(p.moveMsg(unityToAoiY(p.getPosition().getY())));
    }

    private void handlePlayerCollisions(Player player) {
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Integer otherPlayerId : cell.getPlayers()) {
                    Player otherPlayer = aoiSystem.getPlayerById(otherPlayerId);
                    if (player.getId() != otherPlayer.getId() && player.isCollision(otherPlayer)) {
                        // Perform action when collision occurs
                        // Example action: Change player direction
                        player.setDirection(player.getDirection() + 180); // Reverse direction
//                        otherPlayer.setDirection(otherPlayer.getDirection() + 180); // Reverse direction
                        return; // Exit early to handle one collision at a time
                    }
                }
            }
        }
    }

    private void handleMouseClick(MouseEvent e) {
        Player player = PlayerService.nextPlayer();
        player.position(e.getX(), e.getY());
        aoiSystem.addPlayer(player);
        players.add(player);
        clientJoinHandler.addPlayer(player);
        repaint(); // Redraw the grid with the new player and AOI
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawPlayers(g);
    }

    private void drawPlayers(Graphics g) {

        for (Player player : aoiSystem.getPlayers()) {
            g.setColor(player.getColor());
            float radius = player.getRadius();
            int x = (int) player.getPosition().getX();
            int y = (int) player.getPosition().getY();
            g.fillOval(x - 5, y - 5, 10, 10); // Draw player as a small circle
            g.drawOval(x - (int) radius, y - (int) radius,
                    (int) radius * 2, (int) radius * 2); // Draw player's radius
            g.drawString(player.getName(), (int) (x - RADIUS), (int) (y - RADIUS));
        }
    }

    private void drawGrid(Graphics g) {
        int numCells = gridSize / cellSize;
        g.setColor(Color.BLACK);

        for (int i = 0; i <= numCells; i++) {
            int pos = i * cellSize;
            // Draw vertical lines
            g.drawLine(pos, 0, pos, gridSize);
            // Draw horizontal lines
            g.drawLine(0, pos, gridSize, pos);
        }

    }
}


