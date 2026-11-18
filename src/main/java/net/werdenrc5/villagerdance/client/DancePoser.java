package net.werdenrc5.villagerdance.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.werdenrc5.villagerdance.dance.DanceStyle;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class DancePoser {
    private static final float HIP_Y = 12.0F;
    private static final float ARMS_MAX_X_ROT = -0.75F;
    private static final float ARMS_MIN_X_ROT = -1.3F;
    private static final Quaternionf ROTATION = new Quaternionf();
    private static final Vector3f OFFSET = new Vector3f();

    private DancePoser() {
    }

    public static void apply(DanceStyle style, DanceStyle previous, float blend, float progress, ModelPart head,
                             ModelPart body, ModelPart arms, ModelPart rightLeg, ModelPart leftLeg,
                             float ageInTicks, float speed, float previousSpeed) {
        ModelPart[] parts = {head, body, arms, rightLeg, leftLeg};
        float[][] base = capture(parts);
        float[][] from = null;

        if (previous != null && blend < 1.0F) {
            pose(previous, head, body, arms, rightLeg, leftLeg, ageInTicks, previousSpeed);
            from = capture(parts);
            for (int i = 0; i < parts.length; i++) {
                set(parts[i], base[i]);
            }
        }

        pose(style, head, body, arms, rightLeg, leftLeg, ageInTicks, speed);
        float[][] to = capture(parts);

        float[] out = new float[6];
        for (int i = 0; i < parts.length; i++) {
            float[] b = to[i];
            float[] c = base[i];
            for (int j = 0; j < 6; j++) {
                float v = from != null ? Mth.lerp(blend, from[i][j], b[j]) : b[j];
                out[j] = Mth.lerp(progress, c[j], v);
            }
            set(parts[i], out);
        }
    }

    private static float[][] capture(ModelPart[] parts) {
        float[][] out = new float[parts.length][];
        for (int i = 0; i < parts.length; i++) {
            ModelPart p = parts[i];
            out[i] = new float[]{p.x, p.y, p.z, p.xRot, p.yRot, p.zRot};
        }
        return out;
    }

    private static void set(ModelPart p, float[] v) {
        p.x = v[0];
        p.y = v[1];
        p.z = v[2];
        p.xRot = v[3];
        p.yRot = v[4];
        p.zRot = v[5];
    }

    private static void attach(ModelPart head, ModelPart body, ModelPart arms) {
        float tx = body.x;
        float tz = body.z;

        ROTATION.rotationZYX(body.zRot, body.yRot, body.xRot);

        follow(body, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        follow(head, 0.0F, 0.0F, 0.0F, tx, tz);
        follow(arms, 0.0F, 3.0F, -1.0F, tx, tz);

        arms.yRot += body.yRot;
        arms.xRot = Mth.clamp(arms.xRot, ARMS_MIN_X_ROT, ARMS_MAX_X_ROT);
    }

    private static void follow(ModelPart part, float px, float py, float pz, float tx, float tz) {
        float ry = py - HIP_Y;
        OFFSET.set(px, ry, pz);
        ROTATION.transform(OFFSET);
        part.x += tx + OFFSET.x - px;
        part.y += OFFSET.y - ry;
        part.z += tz + OFFSET.z - pz;
    }

    private static void pose(DanceStyle style, ModelPart head, ModelPart body, ModelPart arms,
                             ModelPart rightLeg, ModelPart leftLeg, float ageInTicks, float speed) {
        float t = ageInTicks * speed * 0.27F;

        switch (style) {
            case SWAY -> {
                body.yRot = Mth.sin(t * 0.5F) * 0.25F;
                head.zRot = Mth.cos(t * 0.5F) * 0.15F;
                head.xRot += Mth.sin(t) * 0.1F;
                arms.xRot = -1.05F + Mth.sin(t) * 0.3F;
                arms.zRot = Mth.sin(t * 0.6F) * 0.3F;
                rightLeg.xRot = Mth.sin(t) * 0.4F;
                leftLeg.xRot = -Mth.sin(t) * 0.4F;
            }
            case PIGLIN -> {
                float bob = Mth.sin(t * 2.4F);
                head.x = Mth.sin(t * 1.2F) * 2.0F;
                head.y = bob + 0.4F;
                body.y = bob * 0.35F;
                arms.y = 3.0F + bob * 0.35F;
                rightLeg.xRot = Mth.sin(t * 1.3F) * 0.2F;
                leftLeg.xRot = -Mth.sin(t * 1.3F) * 0.2F;
            }
            case METAL -> {
                float beat = Mth.sin(t * 1.8F);
                head.xRot = 0.5F + beat * 0.6F;
                body.xRot = beat * 0.15F;
                arms.xRot = -1.0F + beat * 0.3F;
                rightLeg.xRot = beat * 0.1F;
                leftLeg.xRot = -beat * 0.1F;
            }
            case TWIRL -> {
                float angle = t * 1.0F;
                head.yRot = angle;
                body.yRot = angle;
                arms.xRot = -1.2F;
                rightLeg.yRot = angle;
                leftLeg.yRot = angle;
            }
            case JIG -> {
                float lift = Math.abs(Mth.sin(t * 1.5F)) * 2.5F;
                head.y = -lift;
                body.y = -lift;
                arms.y = 3.0F - lift;
                arms.xRot = -0.75F - lift * 0.15F;
                rightLeg.y = 12.0F - lift;
                leftLeg.y = 12.0F - lift;
                rightLeg.xRot = -lift * 0.25F;
                leftLeg.xRot = -lift * 0.25F;
            }
            case FEVER -> {
                float point = Mth.sin(t * 1.5F);
                arms.xRot = -1.2F;
                arms.zRot = point * 0.6F;
                head.zRot = point * 0.3F;
                body.yRot = point * 0.5F;
                rightLeg.zRot = -Math.abs(point) * 0.2F;
                leftLeg.zRot = Math.abs(point) * 0.2F;
                rightLeg.xRot = point * 0.3F;
                leftLeg.xRot = -point * 0.3F;
            }
            case GLITCH -> {
                float snap = Math.round(Mth.sin(t * 0.7F) * 2.0F) / 2.0F;
                head.yRot = snap * 0.8F;
                body.yRot = -snap * 0.4F;
                arms.xRot = -1.2F + snap * 0.35F;
                arms.yRot = snap * 0.3F;
                rightLeg.xRot = snap * 0.5F;
                leftLeg.xRot = -snap * 0.5F;
            }
            case JELLY -> {
                float sway = Mth.sin(t * 0.9F);
                body.zRot = sway * 0.3F;
                head.zRot = -sway * 0.45F;
                arms.xRot = -0.9F;
                arms.zRot = sway * 0.3F;
                rightLeg.zRot = sway * 0.2F;
                leftLeg.zRot = sway * 0.2F;
            }
            case CANCAN -> {
                float swing = Mth.sin(t * 1.3F);
                rightLeg.xRot = -Math.max(0.0F, swing) * 1.2F;
                leftLeg.xRot = -Math.max(0.0F, -swing) * 1.2F;
                arms.xRot = -1.0F + swing * 0.25F;
                arms.zRot = swing * 0.2F;
                body.yRot = swing * 0.2F;
                head.zRot = swing * 0.1F;
            }
            case SHIVER -> {
                head.zRot = Mth.sin(t * 2.2F) * 0.12F;
                head.y = Mth.sin(t * 3.0F) * 0.3F;
                body.x = Mth.sin(t * 2.8F) * 0.5F;
                arms.xRot = -0.75F + Mth.sin(t * 2.5F) * 0.12F;
                arms.zRot = Mth.sin(t * 2.1F) * 0.15F;
                rightLeg.xRot = Mth.sin(t * 2.5F) * 0.25F;
                leftLeg.xRot = -Mth.sin(t * 2.5F) * 0.25F;
            }
            case PARROT -> {
                float f = Mth.cos(t * 2.0F);
                float f1 = Mth.sin(t * 2.0F);
                head.x = f;
                head.y = f1;
                head.zRot = f1 * 0.4F;
                body.x = f;
                body.y = f1 * 0.6F;
                arms.y = 3.0F + f1 * 0.6F;
                arms.xRot = -0.8F;
                arms.zRot = Mth.sin(t * 4.0F) * 0.6F;
                rightLeg.xRot = f1 * 0.15F;
                leftLeg.xRot = -f1 * 0.15F;
            }
        }

        attach(head, body, arms);
    }
}