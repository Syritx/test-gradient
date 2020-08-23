import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;

// coordinate system
class Vector2 {

    float x,y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
}

class Vector3 {

    float x,y,z;

    public Vector3(float x, float y,float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}


// main
public class Launcher {

    static int size = 150;
    static Vector2[] gradient = new Vector2[4];
    static Vector3[] map;


    // perlin noise functions
    static float lerp(float min, float max, float value) {
        return value/(max-min)+min;
    }

    static float dotProduct(Vector2 vectorA, Vector2 vectorB) {
        float a = vectorA.x * vectorB.x;
        float b = vectorA.y * vectorB.y;

        float result = (a+b)*.02f;
        return result;
    }

    public static void main(String[] args) {

        map = new Vector3[size*size];

        int id = 0;
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                map[id] = new Vector3(x, 0, z);
                id++;
            }
        }

        int reach = 10;

        // creating for vectors on the corners
        for (int l = 0; l < 1; l++) {
            for (int i = 0; i < gradient.length; i++) {
                Random rx = new Random(), ry = new Random();

                float xReach = rx.nextInt(reach*2)+1-reach;
                float yReach = ry.nextInt(reach*2)+1-reach;

                gradient[i] = new Vector2(xReach, yReach);
            }

            for (int i = 0; i < map.length; i++) {
                Vector2 position = new Vector2(map[i].x, map[i].z);
                Random rx = new Random(), ry = new Random();

                int offsetReach = 4;

                float nx = rx.nextInt(offsetReach*2)+1-offsetReach;
                float nz = ry.nextInt(offsetReach*2)+1-offsetReach;

                position = new Vector2(position.x+nx, position.y+nz);

                Vector2 vectorA = new Vector2(0,0);

                float height = 0;

                for (int g = 0; g < gradient.length; g++) {

                    float x = gradient[g].x-position.x,y = gradient[g].y-position.y;

                    vectorA = new Vector2(x,y);
                    height += dotProduct(vectorA, gradient[g]);
                    height = lerp(0, 1, height);
                }
                map[i] = new Vector3(map[i].x, height, map[i].z);
            }
        }

        new Game(map);
    }
}

// window / renderer
class Game extends JFrame {

    int TILE_SIZE = 5;
    int LENGTH;

    JPanel background;
    Tile[] tiles;

    public Game(Vector3[] map) {

        LENGTH = (int)Math.sqrt(map.length);
        tiles = new Tile[map.length];

        setSize(LENGTH*TILE_SIZE,LENGTH*TILE_SIZE);
        setTitle("text gradient");
        setResizable(false);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        background = new JPanel();
        background.setLayout(null);
        background.setBounds(0,0,LENGTH*TILE_SIZE,LENGTH*TILE_SIZE);

        int id = 0;
        for (int x = 0; x < LENGTH; x++) {
            for (int y = 0; y < LENGTH; y++) {

                if (map[id].y < 0) map[id].y *= -1;

                tiles[id] = new Tile(x,y,map[id].y,TILE_SIZE);
                background.add(tiles[id]);
                id++;
            }
        }

        add(background);
        setVisible(true);
    }
}

// display
class Tile extends JPanel {

    public Tile(int x, int y, float perlin, int scale) {
        setLayout(null);
        setBounds(x*scale, y*scale, scale, scale);

        int rgb = (int)perlin*5;
        rgb = 255 - rgb;

        if (rgb > 255) rgb = 255;
        if (rgb < 0) rgb = 0;

        setBackground(new Color(rgb,rgb,rgb));
    }
}