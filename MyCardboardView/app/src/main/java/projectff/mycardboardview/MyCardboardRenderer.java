package projectff.mycardboardview;

/**
 * Created by meyert on 24.04.2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.rajawali3d.cardboard.RajawaliCardboardRenderer;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;

public class MyCardboardRenderer extends RajawaliCardboardRenderer {

    String SpheremapPath = "";

    public MyCardboardRenderer(Context context, String spheremapPath) {
        super(context); SpheremapPath = spheremapPath;
    }

    @Override
    protected void initScene() {

        DirectionalLight light = new DirectionalLight(0,0,0);
        light.setPosition(0,10,10);
        getCurrentScene().addLight(light);

        Bitmap b = BitmapFactory.decodeFile(SpheremapPath);

        Sphere sphere = createPhotoSphereWithTexture(new Texture("photo",b));

        getCurrentScene().addChild(sphere);
        getCurrentCamera().setFarPlane(2000 );
        getCurrentCamera().setPosition(Vector3.ZERO);
        getCurrentCamera().setFieldOfView(75);
    }

    private static Sphere createPhotoSphereWithTexture(ATexture texture) {

        Material material = new Material();
        material.setColor(0);

        try {
            material.addTexture(texture);
        } catch (ATexture.TextureException e) {
            throw new RuntimeException(e);
        }

        Sphere sphere = new Sphere(1000, 64, 32);
        sphere.setScaleX(-1);
        sphere.setMaterial(material);

        return sphere;
    }
}
