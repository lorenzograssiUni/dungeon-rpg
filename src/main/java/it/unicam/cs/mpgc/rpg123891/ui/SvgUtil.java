package it.unicam.cs.mpgc.rpg123891.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility per caricare un file SVG come ImageView JavaFX,
 * con possibilità di sovrascrivere il colore fill tramite CSS inline.
 */
public class SvgUtil {

    /**
     * Carica un SVG dalla risorsa indicata, sostituisce tutti i fill
     * con il colore hex fornito, poi lo rasterizza come PNG in memoria.
     *
     * @param resourcePath  es. "/assets/icons/arrow.svg"
     * @param fillColorHex  es. "#cccccc"  o  "#D4A96A"
     * @param size          larghezza e altezza in pixel
     * @return ImageView pronto da usare, o null in caso di errore
     */
    public static ImageView load(String resourcePath, String fillColorHex, double size) {
        try (InputStream is = SvgUtil.class.getResourceAsStream(resourcePath)) {
            if (is == null) return null;

            // Leggi SVG come stringa e inietta il fill
            String svg = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            // Sostituisce fill esistenti e aggiunge style globale
            svg = svg.replaceAll("fill=\"[^\"]*\"", "");
            svg = svg.replaceAll("<svg ", "<svg fill=\"" + fillColorHex + "\" ");

            // Rasterizza con Batik
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH,  (float) size);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size);

            byte[] svgBytes = svg.getBytes(StandardCharsets.UTF_8);
            TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgBytes));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(out);
            transcoder.transcode(input, output);

            // Converti BufferedImage -> JavaFX Image
            BufferedImage buf = javax.imageio.ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
            WritableImage fxImg = SwingFXUtils.toFXImage(buf, null);

            ImageView iv = new ImageView(fxImg);
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            return iv;

        } catch (Exception e) {
            return null;
        }
    }
}
