package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderDragon extends RenderLiving
{
    /**
     * Reloads the dragon model if not equal to 4. Presumably a leftover debugging field.
     */
    private static int updateModelState = 0;

    /** An instance of the dragon model in RenderDragon */
    protected ModelDragon modelDragon;

    public RenderDragon()
    {
        super(new ModelDragon(0.0F), 0.5F);
        this.modelDragon = (ModelDragon)this.mainModel;
        this.setRenderPassModel(this.mainModel);
    }

    /**
     * Used to rotate the dragon as a whole in RenderDragon. It's called in the rotateCorpse method.
     */
    protected void rotateDragonBody(EntityDragon par1EntityDragon, float par2, float par3, float par4)
    {
        float f3 = (float)par1EntityDragon.getMovementOffsets(7, par4)[0];
        float f4 = (float)(par1EntityDragon.getMovementOffsets(5, par4)[1] - par1EntityDragon.getMovementOffsets(10, par4)[1]);
        GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(f4 * 10.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.0F, 1.0F);

        if (par1EntityDragon.deathTime > 0)
        {
            float f5 = ((float)par1EntityDragon.deathTime + par4 - 1.0F) / 20.0F * 1.6F;
            f5 = MathHelper.sqrt_float(f5);

            if (f5 > 1.0F)
            {
                f5 = 1.0F;
            }

            GL11.glRotatef(f5 * this.getDeathMaxRotation(par1EntityDragon), 0.0F, 0.0F, 1.0F);
        }
    }

    /**
     * Renders the dragon model. Called by renderModel.
     */
    protected void renderDragonModel(EntityDragon par1EntityDragon, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        if (par1EntityDragon.deathTicks > 0)
        {
            float f6 = (float)par1EntityDragon.deathTicks / 200.0F;
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, f6);
            this.loadTexture("/mob/enderdragon/shuffle.png");
            this.mainModel.render(par1EntityDragon, par2, par3, par4, par5, par6, par7);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glDepthFunc(GL11.GL_EQUAL);
        }

        this.loadTexture(par1EntityDragon.getTexture());
        this.mainModel.render(par1EntityDragon, par2, par3, par4, par5, par6, par7);

        if (par1EntityDragon.hurtTime > 0)
        {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
            this.mainModel.render(par1EntityDragon, par2, par3, par4, par5, par6, par7);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }
    }

    /**
     * Renders the dragon, along with its dying animation
     */
    public void renderDragon(EntityDragon par1EntityDragon, double par2, double par4, double par6, float par8, float par9)
    {
        BossStatus.func_82824_a(par1EntityDragon, false);

        if (updateModelState != 4)
        {
            this.mainModel = new ModelDragon(0.0F);
            updateModelState = 4;
        }

        super.doRenderLiving(par1EntityDragon, par2, par4, par6, par8, par9);

        if (par1EntityDragon.healingEnderCrystal != null)
        {
            float f2 = (float)par1EntityDragon.healingEnderCrystal.innerRotation + par9;
            float f3 = MathHelper.sin(f2 * 0.2F) / 2.0F + 0.5F;
            f3 = (f3 * f3 + f3) * 0.2F;
            float f4 = (float)(par1EntityDragon.healingEnderCrystal.posX - par1EntityDragon.posX - (par1EntityDragon.prevPosX - par1EntityDragon.posX) * (double)(1.0F - par9));
            float f5 = (float)((double)f3 + par1EntityDragon.healingEnderCrystal.posY - 1.0D - par1EntityDragon.posY - (par1EntityDragon.prevPosY - par1EntityDragon.posY) * (double)(1.0F - par9));
            float f6 = (float)(par1EntityDragon.healingEnderCrystal.posZ - par1EntityDragon.posZ - (par1EntityDragon.prevPosZ - par1EntityDragon.posZ) * (double)(1.0F - par9));
            float f7 = MathHelper.sqrt_float(f4 * f4 + f6 * f6);
            float f8 = MathHelper.sqrt_float(f4 * f4 + f5 * f5 + f6 * f6);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par2, (float)par4 + 2.0F, (float)par6);
            GL11.glRotatef((float)(-Math.atan2((double)f6, (double)f4)) * 180.0F / (float)Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float)(-Math.atan2((double)f7, (double)f5)) * 180.0F / (float)Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
            Tessellator tessellator = Tessellator.instance;
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_CULL_FACE);
            this.loadTexture("/mob/enderdragon/beam.png");
            GL11.glShadeModel(GL11.GL_SMOOTH);
            float f9 = 0.0F - ((float)par1EntityDragon.ticksExisted + par9) * 0.01F;
            float f10 = MathHelper.sqrt_float(f4 * f4 + f5 * f5 + f6 * f6) / 32.0F - ((float)par1EntityDragon.ticksExisted + par9) * 0.01F;
            tessellator.startDrawing(5);
            byte b0 = 8;

            for (int i = 0; i <= b0; ++i)
            {
                float f11 = MathHelper.sin((float)(i % b0) * (float)Math.PI * 2.0F / (float)b0) * 0.75F;
                float f12 = MathHelper.cos((float)(i % b0) * (float)Math.PI * 2.0F / (float)b0) * 0.75F;
                float f13 = (float)(i % b0) * 1.0F / (float)b0;
                tessellator.setColorOpaque_I(0);
                tessellator.addVertexWithUV((double)(f11 * 0.2F), (double)(f12 * 0.2F), 0.0D, (double)f13, (double)f10);
                tessellator.setColorOpaque_I(16777215);
                tessellator.addVertexWithUV((double)f11, (double)f12, (double)f8, (double)f13, (double)f9);
            }

            tessellator.draw();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glShadeModel(GL11.GL_FLAT);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
        }
    }

    /**
     * Renders the animation for when an enderdragon dies
     */
    protected void renderDragonDying(EntityDragon par1EntityDragon, float par2)
    {
        super.renderEquippedItems(par1EntityDragon, par2);
        Tessellator tessellator = Tessellator.instance;

        if (par1EntityDragon.deathTicks > 0)
        {
            RenderHelper.disableStandardItemLighting();
            float f1 = ((float)par1EntityDragon.deathTicks + par2) / 200.0F;
            float f2 = 0.0F;

            if (f1 > 0.8F)
            {
                f2 = (f1 - 0.8F) / 0.2F;
            }

            Random random = new Random(432L);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDepthMask(false);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -1.0F, -2.0F);

            for (int i = 0; (float)i < (f1 + f1 * f1) / 2.0F * 60.0F; ++i)
            {
                GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
                tessellator.startDrawing(6);
                float f3 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
                float f4 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
                tessellator.setColorRGBA_I(16777215, (int)(255.0F * (1.0F - f2)));
                tessellator.addVertex(0.0D, 0.0D, 0.0D);
                tessellator.setColorRGBA_I(16711935, 0);
                tessellator.addVertex(-0.866D * (double)f4, (double)f3, (double)(-0.5F * f4));
                tessellator.addVertex(0.866D * (double)f4, (double)f3, (double)(-0.5F * f4));
                tessellator.addVertex(0.0D, (double)f3, (double)(1.0F * f4));
                tessellator.addVertex(-0.866D * (double)f4, (double)f3, (double)(-0.5F * f4));
                tessellator.draw();
            }

            GL11.glPopMatrix();
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            RenderHelper.enableStandardItemLighting();
        }
    }

    /**
     * Renders the overlay for glowing eyes and the mouth. Called by shouldRenderPass.
     */
    protected int renderGlow(EntityDragon par1EntityDragon, int par2, float par3)
    {
        if (par2 == 1)
        {
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        if (par2 != 0)
        {
            return -1;
        }
        else
        {
            this.loadTexture("/mob/enderdragon/ender_eyes.png");
            float f1 = 1.0F;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_EQUAL);
            char c0 = 61680;
            int j = c0 % 65536;
            int k = c0 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f1);
            return 1;
        }
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.renderGlow((EntityDragon)par1EntityLiving, par2, par3);
    }

    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        this.renderDragonDying((EntityDragon)par1EntityLiving, par2);
    }

    protected void rotateCorpse(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        this.rotateDragonBody((EntityDragon)par1EntityLiving, par2, par3, par4);
    }

    /**
     * Renders the model in RenderLiving
     */
    protected void renderModel(EntityLiving par1EntityLiving, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.renderDragonModel((EntityDragon)par1EntityLiving, par2, par3, par4, par5, par6, par7);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderDragon((EntityDragon)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderDragon((EntityDragon)par1Entity, par2, par4, par6, par8, par9);
    }
}
