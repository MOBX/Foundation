package lamfire;

import java.io.File;

import com.lamfire.utils.PictureMarker;

/**
 * @author zxc Jul 18, 2015 1:47:52 PM
 */
public class PictureMarkerTest {

    public static void main(String[] args) {
        File source = new File("D:\\data\\source.jpg");
        File target = new File("D:\\data\\target.jpg");
        PictureMarker.markImage(source, target);
    }
}
