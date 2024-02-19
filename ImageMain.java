import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageMain {

    // function that converts an image of any format into an object of type BufferedImage
    public static BufferedImage convertToBufferedImage(String imagePath) {
        try {
            // Read the image file into a BufferedImage object
            File file = new File(imagePath);
            BufferedImage image = ImageIO.read(file);
            return image;
        } catch (IOException e) {
            // Handle any IO exceptions (e.g., file not found)
            e.printStackTrace();
            return null;
        }
    }

    // function that returns the 4 extreme (most distant) non-transparent points of the image
    public static int[] cardinalCoordinates(BufferedImage image) {

        // The image's dimensions :
        int width = image.getWidth();
        int height = image.getHeight();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        // Find the bounding box of non-transparent pixels
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (image.getRGB(x, y) != 0) { // Non-transparent pixel
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        int[] cardinalPoints = { minX, minY, maxX, maxY };
        return cardinalPoints;
    }

    /*
     * Start by creating a function isAvatar that determines whether the
     * non-transparents pixels lies within a circle which edges are only constituted
     * of non-transparent pixels. This circle also has to remain inside the image.
     */
    public static boolean isAvatar(BufferedImage image, int minX, int minY, int maxX, int maxY) {

        /*
         * start by determining the coordinates of the circle and its radius so it
         * become possible to do operations base on it later on.
         * 
         */
        int centerX = (minX + maxX) / 2;
        int centerY = (minY + maxY) / 2;
        int radius = (Math.max(maxX - minX, maxY - minY)) / 2; // Assuming circle is centered

        /*
         * Check whether the circle of non-transparent pixels remains inside the image.
         * If the centers of mass for X and Y coordinates are higher than the radius,
         * then the circle's boundaries go beyond the image's scope.
         */
        if (centerX - radius < 0 || centerY - radius < 0 || centerX + radius >= image.getWidth()
                || centerY + radius >= image.getHeight()) {
            System.out.println("Circle extends beyond image boundaries");
            return false;
        }

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            /*
             * along the x-axis of the circle, compute the correspond coordinate y for each
             * pixel coontained in the superior part of the circle.
             */

            int y = (int) Math.round(centerY + Math.sqrt(radius * radius - (x - centerX) * (x - centerX)));

            // Check if the circle remains inside the image
            if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) {
                int pixelColor = image.getRGB(x, y);
                // Start by extracting the alpha value
                int alpha = (pixelColor >> 24) & 0xFF;
                // A non-transparent pixiel has its channel alpha set to 0.
                if (alpha == 0) {
                    return false;
                }
            } else {
                System.out.println("Circle extends beyond image boundaries");
                return false;
            }

            /*
             * along the x-axis of the circle, compute the correspond coordinate y for each
             * pixel contained in the bottom part of the circle.
             */
            if (y != centerY) {
                int lowerY = centerY
                        - (int) Math.round(Math.sqrt(radius * radius - (x - centerX) * (x - centerX)));
                if (x >= 0 && lowerY >= 0 && x < image.getWidth() && lowerY < image.getHeight()) {
                    int pixelColor = image.getRGB(x, lowerY);
                    int alpha = (pixelColor >> 24) & 0xFF;
                    if (alpha == 0) {
                        return false;
                    }
                } else {
                    System.out.println("Circle extends beyond image boundaries");
                    return false;
                }
            }
        }

        // Tous les pixels à l'intérieur du cercle sont non transparents
        return true;
    }

    // Function to check if a pixel's color falls within any of the specified sad
    // color ranges
    public static boolean isSadPixel(int red, int green, int blue) {
        /*
         * The feelings generated by a set of colours may not be the same for eachh of
         * us. So, this is rather subjective, I set my own criteria for determining
         * what types of colours gives a "sad" feeling. It is easier to determine if an
         * image gives a sad feeling rather than a happy feeling.
         */
        int[][] SAD_COLOR_RANGES = { { 0, 108, 0, 108, 0, 255 }, // Dark Blue
                { 0, 0, 0, 192, 192, 255 }, // Other shades of blue that tend to give a sad feeling
                { 0, 0, 64, 128, 64, 128 }, // Dark Grayish Blue
                { 0, 0, 128, 192, 128, 192 }, // Medium Grayish Blue
                { 0, 0, 128, 255, 128, 255 }, // Dark Purple to Medium Purple

                { 32, 160, 0, 96, 0, 96 }, // Extended Dark Brown
                { 0, 128, 0, 64, 0, 64 }, // Dark Brown
                { 64, 192, 0, 128, 0, 128 }, // Medium Brown

                { 0, 64, 0, 64, 0, 64 }, // Black
                { 0, 255, 0, 83, 0, 83 }, // Dark Red
                { 128, 255, 0, 128, 0, 128 }, // Medium Red to Dark Red
                { 64, 192, 0, 128, 0, 128 } // Extended Medium Red
        };
        for (int[] range : SAD_COLOR_RANGES) {
            if (red >= range[0] && red <= range[1] && green >= range[2] && green <= range[3] && blue >= range[4]
                    && blue <= range[5]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSadBadge(BufferedImage image, int minX, int minY, int maxX, int maxY) {

        int countSadPixels = 0;
        int countPixels = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                int pixelColor = image.getRGB(x, y);
                int red = (pixelColor >> 16) & 0xFF;
                int green = (pixelColor >> 8) & 0xFF;
                int blue = pixelColor & 0xFF;

                if (isSadPixel(red, green, blue)) {
                    countSadPixels++;
                }
                countPixels++;
            }
        }

        double proportionSadPixels = (double) countSadPixels / countPixels;

        return proportionSadPixels > 0.5;
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("This is what you should write in the terminal: java ImageMain <image_path>");
            return;
        }

        String path = args[0];

        BufferedImage image = convertToBufferedImage(path);

        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();

            System.out.println("Image dimensions: " + width + " x " + height);

            if (width == 512 && height == 512) {
                int[] cardinalPoints = cardinalCoordinates(image);

                if (isAvatar(image, cardinalPoints[0], cardinalPoints[1], cardinalPoints[2], cardinalPoints[3])) {
                    System.out.println("The image is an avatar.");

                    if (isSadBadge(image, cardinalPoints[0], cardinalPoints[1], cardinalPoints[2],
                            cardinalPoints[3])) {
                        System.out.println("The avatar tends to give a sad feeling.");
                    } else {
                        System.out.println("The avatar tends to give a happy feeling.");
                    }
                } else {
                    System.out.println("The image is not an avatar.");
                }
            } else {
                System.out.println("Wrong Image dimensions!");
            }
        } else {
            System.out.println("Failed to load the image.");
        }
    }
}
