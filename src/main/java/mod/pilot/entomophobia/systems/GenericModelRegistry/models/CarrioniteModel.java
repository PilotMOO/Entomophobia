package mod.pilot.entomophobia.systems.GenericModelRegistry.models;// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.pilot.entomophobia.Entomophobia;
import mod.pilot.entomophobia.systems.GenericModelRegistry.IGenericModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CarrioniteModel extends Model implements IGenericModel {
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Entomophobia.MOD_ID,
			"textures/misc/carrionite_texture.png");
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(RESOURCE_LOCATION, "main");
	public final ModelPart carrionite;
	public final ModelPart Limbs;
	public final ModelPart limb1;
	public final ModelPart Seg2limb1;
	public final ModelPart Seg3limb1;
	public final ModelPart limb2;
	public final ModelPart Seg2limb2;
	public final ModelPart Seg3limb2;
	public final ModelPart limb3;
	public final ModelPart Seg2limb3;
	public final ModelPart Seg3limb3;
	public final ModelPart limb4;
	public final ModelPart Seg2limb4;
	public final ModelPart Seg3limb4;
	public final ModelPart limb5;
	public final ModelPart Seg2limb5;
	public final ModelPart Seg3limb5;
	public final ModelPart Wings;
	public final ModelPart Wing1;
	public final ModelPart Wing2;
	public final ModelPart Wing3;
	public final ModelPart Wing4;
	public final ModelPart EyeMasses;
	public final ModelPart EyeMass1;
	public final ModelPart EyeMass2;
	public final ModelPart EyeMass3;
	public final ModelPart mass;
	public final ModelPart EyeMassCombs;
	public final ModelPart bundle1;
	public final ModelPart comb1;
	public final ModelPart comb2;
	public final ModelPart bundle2;
	public final ModelPart comb3;
	public final ModelPart comb4;
	public final ModelPart comb5;
	public final ModelPart bundle3;
	public final ModelPart comb7;
	public final ModelPart comb8;
	public final ModelPart bundle4;
	public final ModelPart comb6;
	public final ModelPart comb9;
	public final ModelPart limbsAndEtra;
	public final ModelPart Smalltendril1;
	public final ModelPart Seg2tendril1;
	public final ModelPart Seg3tendril1;
	public final ModelPart Seg4tendril1;
	public final ModelPart Seg5tendril1;
	public final ModelPart Smalltendril2;
	public final ModelPart Seg2tendril2;
	public final ModelPart Seg3tendril2;
	public final ModelPart Seg4tendril2;
	public final ModelPart Seg5tendril2;
	public final ModelPart EyeMass4;
	public final ModelPart Combs;
	public final ModelPart LargeComb1;
	public final ModelPart Lcomb10;
	public final ModelPart Lcomb11;
	public final ModelPart LargeComb2;
	public final ModelPart Lcomb12;
	public final ModelPart Lcomb13;
	public final ModelPart Lcomb14;
	public final ModelPart LargeComb3;
	public final ModelPart Lcomb15;
	public final ModelPart Lcomb16;
	public final ModelPart LargeComb4;
	public final ModelPart Lcomb17;
	public final ModelPart Lcomb18;
	public final ModelPart LargeComb5;
	public final ModelPart Lcomb19;
	public final ModelPart Lcomb20;
	public final ModelPart Lcomb21;
	public final ModelPart LargeComb6;
	public final ModelPart Lcomb22;
	public final ModelPart Lcomb23;
	public final ModelPart Tendrils;
	public final ModelPart LargeTendril1;
	public final ModelPart Seg2LargeTendril1;
	public final ModelPart Seg3LargeTendril1;
	public final ModelPart Seg4LargeTendril1;
	public final ModelPart Seg5LargeTendril1;
	public final ModelPart LargeTendril2;
	public final ModelPart Seg2LargeTendril2;
	public final ModelPart Seg3LargeTendril2;
	public final ModelPart Seg4LargeTendril2;
	public final ModelPart Seg5LargeTendril2;
	public final ModelPart LargeTendril3;
	public final ModelPart Seg2LargeTendril3;
	public final ModelPart Seg3LargeTendril3;
	public final ModelPart Seg4LargeTendril3;
	public final ModelPart Seg5LargeTendril3;
	public final ModelPart LargeTendril4;
	public final ModelPart Seg2LargeTendril4;
	public final ModelPart Seg3LargeTendril4;
	public final ModelPart Seg4LargeTendril4;
	public final ModelPart Seg5LargeTendril4;
	public final ModelPart LargeTendril5;
	public final ModelPart Seg2LargeTendril5;
	public final ModelPart Seg3LargeTendril5;
	public final ModelPart Seg4LargeTendril5;
	public final ModelPart Seg5LargeTendril5;
	public final ModelPart LargeTendril6;
	public final ModelPart Seg2LargeTendril6;
	public final ModelPart Seg3LargeTendril6;
	public final ModelPart Seg4LargeTendril6;
	public final ModelPart Seg5LargeTendril6;
	public final ModelPart LargeTendril7;
	public final ModelPart Seg2LargeTendril7;
	public final ModelPart Seg3LargeTendril7;
	public final ModelPart Seg4LargeTendril7;
	public final ModelPart Seg5LargeTendril7;

	public CarrioniteModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.carrionite = root.getChild("carrionite");
		this.Limbs = this.carrionite.getChild("Limbs");
		this.limb1 = this.Limbs.getChild("limb1");
		this.Seg2limb1 = this.limb1.getChild("Seg2limb1");
		this.Seg3limb1 = this.Seg2limb1.getChild("Seg3limb1");
		this.limb2 = this.Limbs.getChild("limb2");
		this.Seg2limb2 = this.limb2.getChild("Seg2limb2");
		this.Seg3limb2 = this.Seg2limb2.getChild("Seg3limb2");
		this.limb3 = this.Limbs.getChild("limb3");
		this.Seg2limb3 = this.limb3.getChild("Seg2limb3");
		this.Seg3limb3 = this.Seg2limb3.getChild("Seg3limb3");
		this.limb4 = this.Limbs.getChild("limb4");
		this.Seg2limb4 = this.limb4.getChild("Seg2limb4");
		this.Seg3limb4 = this.Seg2limb4.getChild("Seg3limb4");
		this.limb5 = this.Limbs.getChild("limb5");
		this.Seg2limb5 = this.limb5.getChild("Seg2limb5");
		this.Seg3limb5 = this.Seg2limb5.getChild("Seg3limb5");
		this.Wings = this.carrionite.getChild("Wings");
		this.Wing1 = this.Wings.getChild("Wing1");
		this.Wing2 = this.Wings.getChild("Wing2");
		this.Wing3 = this.Wings.getChild("Wing3");
		this.Wing4 = this.Wings.getChild("Wing4");
		this.EyeMasses = this.carrionite.getChild("EyeMasses");
		this.EyeMass1 = this.EyeMasses.getChild("EyeMass1");
		this.EyeMass2 = this.EyeMasses.getChild("EyeMass2");
		this.EyeMass3 = this.EyeMasses.getChild("EyeMass3");
		this.mass = this.EyeMass3.getChild("mass");
		this.EyeMassCombs = this.mass.getChild("EyeMassCombs");
		this.bundle1 = this.EyeMassCombs.getChild("bundle1");
		this.comb1 = this.bundle1.getChild("comb1");
		this.comb2 = this.bundle1.getChild("comb2");
		this.bundle2 = this.EyeMassCombs.getChild("bundle2");
		this.comb3 = this.bundle2.getChild("comb3");
		this.comb4 = this.bundle2.getChild("comb4");
		this.comb5 = this.bundle2.getChild("comb5");
		this.bundle3 = this.EyeMassCombs.getChild("bundle3");
		this.comb7 = this.bundle3.getChild("comb7");
		this.comb8 = this.bundle3.getChild("comb8");
		this.bundle4 = this.EyeMassCombs.getChild("bundle4");
		this.comb6 = this.bundle4.getChild("comb6");
		this.comb9 = this.bundle4.getChild("comb9");
		this.limbsAndEtra = this.EyeMass3.getChild("limbsAndEtra");
		this.Smalltendril1 = this.limbsAndEtra.getChild("Smalltendril1");
		this.Seg2tendril1 = this.Smalltendril1.getChild("Seg2tendril1");
		this.Seg3tendril1 = this.Seg2tendril1.getChild("Seg3tendril1");
		this.Seg4tendril1 = this.Seg3tendril1.getChild("Seg4tendril1");
		this.Seg5tendril1 = this.Seg4tendril1.getChild("Seg5tendril1");
		this.Smalltendril2 = this.limbsAndEtra.getChild("Smalltendril2");
		this.Seg2tendril2 = this.Smalltendril2.getChild("Seg2tendril2");
		this.Seg3tendril2 = this.Seg2tendril2.getChild("Seg3tendril2");
		this.Seg4tendril2 = this.Seg3tendril2.getChild("Seg4tendril2");
		this.Seg5tendril2 = this.Seg4tendril2.getChild("Seg5tendril2");
		this.EyeMass4 = this.EyeMasses.getChild("EyeMass4");
		this.Combs = this.carrionite.getChild("Combs");
		this.LargeComb1 = this.Combs.getChild("LargeComb1");
		this.Lcomb10 = this.LargeComb1.getChild("Lcomb10");
		this.Lcomb11 = this.LargeComb1.getChild("Lcomb11");
		this.LargeComb2 = this.Combs.getChild("LargeComb2");
		this.Lcomb12 = this.LargeComb2.getChild("Lcomb12");
		this.Lcomb13 = this.LargeComb2.getChild("Lcomb13");
		this.Lcomb14 = this.LargeComb2.getChild("Lcomb14");
		this.LargeComb3 = this.Combs.getChild("LargeComb3");
		this.Lcomb15 = this.LargeComb3.getChild("Lcomb15");
		this.Lcomb16 = this.LargeComb3.getChild("Lcomb16");
		this.LargeComb4 = this.Combs.getChild("LargeComb4");
		this.Lcomb17 = this.LargeComb4.getChild("Lcomb17");
		this.Lcomb18 = this.LargeComb4.getChild("Lcomb18");
		this.LargeComb5 = this.Combs.getChild("LargeComb5");
		this.Lcomb19 = this.LargeComb5.getChild("Lcomb19");
		this.Lcomb20 = this.LargeComb5.getChild("Lcomb20");
		this.Lcomb21 = this.LargeComb5.getChild("Lcomb21");
		this.LargeComb6 = this.Combs.getChild("LargeComb6");
		this.Lcomb22 = this.LargeComb6.getChild("Lcomb22");
		this.Lcomb23 = this.LargeComb6.getChild("Lcomb23");
		this.Tendrils = this.carrionite.getChild("Tendrils");
		this.LargeTendril1 = this.Tendrils.getChild("LargeTendril1");
		this.Seg2LargeTendril1 = this.LargeTendril1.getChild("Seg2LargeTendril1");
		this.Seg3LargeTendril1 = this.Seg2LargeTendril1.getChild("Seg3LargeTendril1");
		this.Seg4LargeTendril1 = this.Seg3LargeTendril1.getChild("Seg4LargeTendril1");
		this.Seg5LargeTendril1 = this.Seg4LargeTendril1.getChild("Seg5LargeTendril1");
		this.LargeTendril2 = this.Tendrils.getChild("LargeTendril2");
		this.Seg2LargeTendril2 = this.LargeTendril2.getChild("Seg2LargeTendril2");
		this.Seg3LargeTendril2 = this.Seg2LargeTendril2.getChild("Seg3LargeTendril2");
		this.Seg4LargeTendril2 = this.Seg3LargeTendril2.getChild("Seg4LargeTendril2");
		this.Seg5LargeTendril2 = this.Seg4LargeTendril2.getChild("Seg5LargeTendril2");
		this.LargeTendril3 = this.Tendrils.getChild("LargeTendril3");
		this.Seg2LargeTendril3 = this.LargeTendril3.getChild("Seg2LargeTendril3");
		this.Seg3LargeTendril3 = this.Seg2LargeTendril3.getChild("Seg3LargeTendril3");
		this.Seg4LargeTendril3 = this.Seg3LargeTendril3.getChild("Seg4LargeTendril3");
		this.Seg5LargeTendril3 = this.Seg4LargeTendril3.getChild("Seg5LargeTendril3");
		this.LargeTendril4 = this.Tendrils.getChild("LargeTendril4");
		this.Seg2LargeTendril4 = this.LargeTendril4.getChild("Seg2LargeTendril4");
		this.Seg3LargeTendril4 = this.Seg2LargeTendril4.getChild("Seg3LargeTendril4");
		this.Seg4LargeTendril4 = this.Seg3LargeTendril4.getChild("Seg4LargeTendril4");
		this.Seg5LargeTendril4 = this.Seg4LargeTendril4.getChild("Seg5LargeTendril4");
		this.LargeTendril5 = this.Tendrils.getChild("LargeTendril5");
		this.Seg2LargeTendril5 = this.LargeTendril5.getChild("Seg2LargeTendril5");
		this.Seg3LargeTendril5 = this.Seg2LargeTendril5.getChild("Seg3LargeTendril5");
		this.Seg4LargeTendril5 = this.Seg3LargeTendril5.getChild("Seg4LargeTendril5");
		this.Seg5LargeTendril5 = this.Seg4LargeTendril5.getChild("Seg5LargeTendril5");
		this.LargeTendril6 = this.Tendrils.getChild("LargeTendril6");
		this.Seg2LargeTendril6 = this.LargeTendril6.getChild("Seg2LargeTendril6");
		this.Seg3LargeTendril6 = this.Seg2LargeTendril6.getChild("Seg3LargeTendril6");
		this.Seg4LargeTendril6 = this.Seg3LargeTendril6.getChild("Seg4LargeTendril6");
		this.Seg5LargeTendril6 = this.Seg4LargeTendril6.getChild("Seg5LargeTendril6");
		this.LargeTendril7 = this.Tendrils.getChild("LargeTendril7");
		this.Seg2LargeTendril7 = this.LargeTendril7.getChild("Seg2LargeTendril7");
		this.Seg3LargeTendril7 = this.Seg2LargeTendril7.getChild("Seg3LargeTendril7");
		this.Seg4LargeTendril7 = this.Seg3LargeTendril7.getChild("Seg4LargeTendril7");
		this.Seg5LargeTendril7 = this.Seg4LargeTendril7.getChild("Seg5LargeTendril7");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition carrionite = partdefinition.addOrReplaceChild("carrionite", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Mass_r1 = carrionite.addOrReplaceChild("Mass_r1", CubeListBuilder.create().texOffs(190, 113).addBox(-11.3141F, -9.8551F, -42.9264F, 31.0F, 32.0F, 45.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.818F, -7.6106F, 7.6795F, -0.2509F, -0.3106F, -0.5345F));

		PartDefinition Mass_r2 = carrionite.addOrReplaceChild("Mass_r2", CubeListBuilder.create().texOffs(0, 190).addBox(-11.0264F, 4.0591F, -43.1364F, 31.0F, 32.0F, 56.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.818F, -7.6106F, 7.6795F, -0.2509F, 0.3106F, 0.5345F));

		PartDefinition Mass_r3 = carrionite.addOrReplaceChild("Mass_r3", CubeListBuilder.create().texOffs(0, 113).addBox(-23.383F, -9.7355F, -34.7557F, 44.0F, 26.0F, 51.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.818F, -7.6106F, 7.6795F, -0.5208F, -0.0184F, -0.0912F));

		PartDefinition Mass_r4 = carrionite.addOrReplaceChild("Mass_r4", CubeListBuilder.create().texOffs(218, 46).addBox(-19.2193F, -30.184F, 5.5132F, 40.0F, 32.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.818F, -7.6106F, 7.6795F, -0.4662F, -0.126F, -0.1114F));

		PartDefinition Mass_r5 = carrionite.addOrReplaceChild("Mass_r5", CubeListBuilder.create().texOffs(174, 190).addBox(-18.6886F, -23.9411F, -24.2948F, 36.0F, 10.0F, 44.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.818F, -7.6106F, 7.6795F, 0.0425F, -0.0262F, 0.0832F));

		PartDefinition Mass_r6 = carrionite.addOrReplaceChild("Mass_r6", CubeListBuilder.create().texOffs(218, 0).addBox(-19.3549F, 17.8882F, -11.0592F, 31.0F, 13.0F, 33.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.818F, -7.6106F, 7.6795F, 0.3979F, 0.1753F, 0.1672F));

		PartDefinition Mass_r7 = carrionite.addOrReplaceChild("Mass_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-18.698F, -13.9411F, -40.2948F, 36.0F, 40.0F, 73.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.818F, -7.6106F, 7.6795F, 0.1734F, -0.0262F, 0.0832F));

		PartDefinition Limbs = carrionite.addOrReplaceChild("Limbs", CubeListBuilder.create(), PartPose.offset(-8.5874F, -26.6085F, -11.8111F));

		PartDefinition limb1 = Limbs.addOrReplaceChild("limb1", CubeListBuilder.create().texOffs(232, 284).addBox(-2.5F, -26.0F, -2.5F, 5.0F, 26.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.3054F, 0.0F));

		PartDefinition Seg2limb1 = limb1.addOrReplaceChild("Seg2limb1", CubeListBuilder.create().texOffs(282, 397).addBox(-3.0F, -14.25F, -2.0F, 5.0F, 15.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.5F, -24.75F, -0.5F, 0.48F, 0.0F, 0.0F));

		PartDefinition Seg3limb1 = Seg2limb1.addOrReplaceChild("Seg3limb1", CubeListBuilder.create().texOffs(342, 96).addBox(-3.5F, -2.0F, -1.0F, 5.0F, 5.0F, 14.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.5F, -13.25F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition plane_r1 = Seg3limb1.addOrReplaceChild("plane_r1", CubeListBuilder.create().texOffs(254, 276).addBox(0.0F, -1.0F, -18.5F, 0.0F, 14.0F, 27.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.25F, 18.0F, -0.0873F, 0.0F, 0.0F));

		PartDefinition limbtip_r1 = Seg3limb1.addOrReplaceChild("limbtip_r1", CubeListBuilder.create().texOffs(352, 274).addBox(-2.5F, -0.5F, -10.0F, 4.0F, 3.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.25F, 22.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition limb2 = Limbs.addOrReplaceChild("limb2", CubeListBuilder.create().texOffs(234, 285).mirror().addBox(-2.5F, -26.0F, -1.5F, 4.0F, 26.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(27.0F, 13.0F, -19.0F, 0.2174F, -0.0802F, 1.0344F));

		PartDefinition Seg2limb2 = limb2.addOrReplaceChild("Seg2limb2", CubeListBuilder.create().texOffs(284, 398).addBox(-3.0F, -14.25F, -1.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.5F, -24.75F, -0.5F, 0.48F, 0.0F, 0.0F));

		PartDefinition Seg3limb2 = Seg2limb2.addOrReplaceChild("Seg3limb2", CubeListBuilder.create().texOffs(344, 97).addBox(-3.5F, -2.0F, -1.0F, 4.0F, 5.0F, 13.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.5F, -13.25F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition plane_r2 = Seg3limb2.addOrReplaceChild("plane_r2", CubeListBuilder.create().texOffs(260, 282).addBox(0.0F, -1.0F, -18.5F, 0.0F, 12.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.75F, 18.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition limbtip_r2 = Seg3limb2.addOrReplaceChild("limbtip_r2", CubeListBuilder.create().texOffs(354, 275).addBox(-2.5F, -0.5F, -9.0F, 3.0F, 3.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.25F, 20.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition limb3 = Limbs.addOrReplaceChild("limb3", CubeListBuilder.create().texOffs(234, 285).mirror().addBox(-2.5F, -26.0F, -2.5F, 4.0F, 26.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.9335F, 22.7125F, 25.9467F, -0.4468F, -0.3493F, -1.7497F));

		PartDefinition Seg2limb3 = limb3.addOrReplaceChild("Seg2limb3", CubeListBuilder.create().texOffs(284, 398).addBox(-3.0F, -14.25F, -3.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.5F, -24.75F, 0.5F, -0.48F, 0.0F, 0.0F));

		PartDefinition Seg3limb3 = Seg2limb3.addOrReplaceChild("Seg3limb3", CubeListBuilder.create().texOffs(344, 97).addBox(-3.5F, -2.0F, -12.0F, 4.0F, 5.0F, 13.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.5F, -13.25F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition plane_r3 = Seg3limb3.addOrReplaceChild("plane_r3", CubeListBuilder.create().texOffs(261, 282).addBox(0.0F, -6.0F, -11.5F, 0.0F, 12.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 5.6209F, -10.5909F, 3.0543F, 0.0F, 3.1416F));

		PartDefinition limbtip_r3 = Seg3limb3.addOrReplaceChild("limbtip_r3", CubeListBuilder.create().texOffs(354, 275).addBox(-2.5F, -0.5F, -4.0F, 3.0F, 3.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.25F, -20.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition limb4 = Limbs.addOrReplaceChild("limb4", CubeListBuilder.create().texOffs(236, 286).mirror().addBox(-1.5F, -16.0F, -1.5F, 3.0F, 17.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-14.1466F, 27.3075F, 27.9096F, -0.3315F, -0.8341F, -1.9795F));

		PartDefinition Seg2limb4 = limb4.addOrReplaceChild("Seg2limb4", CubeListBuilder.create().texOffs(286, 399).addBox(-2.0F, -14.25F, -3.0F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.5F, -14.75F, 1.5F, -0.48F, 0.0F, 0.0F));

		PartDefinition Seg3limb4 = Seg2limb4.addOrReplaceChild("Seg3limb4", CubeListBuilder.create().texOffs(346, 98).addBox(-2.5F, -1.0F, -12.0F, 3.0F, 4.0F, 12.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.5F, -13.25F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition plane_r4 = Seg3limb4.addOrReplaceChild("plane_r4", CubeListBuilder.create().texOffs(260, 283).addBox(0.0F, 0.0F, -4.5F, 0.0F, 11.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.25F, -18.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition limbtip_r4 = Seg3limb4.addOrReplaceChild("limbtip_r4", CubeListBuilder.create().texOffs(356, 276).addBox(-1.0F, -1.0F, -11.0F, 2.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.8235F, -12.3641F, 0.3054F, 0.0F, 0.0F));

		PartDefinition limb5 = Limbs.addOrReplaceChild("limb5", CubeListBuilder.create().texOffs(234, 285).mirror().addBox(-2.5F, -26.0F, -2.5F, 4.0F, 26.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0665F, 39.6573F, -12.7039F, 2.1645F, 0.1719F, 0.3053F));

		PartDefinition Seg2limb5 = limb5.addOrReplaceChild("Seg2limb5", CubeListBuilder.create().texOffs(284, 398).addBox(-3.0F, -14.25F, -3.0F, 4.0F, 15.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.5F, -24.75F, 0.5F, -0.48F, 0.0F, 0.0F));

		PartDefinition Seg3limb5 = Seg2limb5.addOrReplaceChild("Seg3limb5", CubeListBuilder.create().texOffs(344, 97).addBox(-3.5F, -2.0F, -12.0F, 4.0F, 5.0F, 13.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.5F, -13.25F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition plane_r5 = Seg3limb5.addOrReplaceChild("plane_r5", CubeListBuilder.create().texOffs(258, 282).addBox(0.0F, -1.0F, -4.5F, 0.0F, 12.0F, 23.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.25F, -18.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition limbtip_r5 = Seg3limb5.addOrReplaceChild("limbtip_r5", CubeListBuilder.create().texOffs(354, 275).addBox(-2.5F, -0.5F, -4.0F, 3.0F, 3.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.25F, -20.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition Wings = carrionite.addOrReplaceChild("Wings", CubeListBuilder.create(), PartPose.offset(0.818F, -7.6106F, 7.6795F));

		PartDefinition Wing1 = Wings.addOrReplaceChild("Wing1", CubeListBuilder.create().texOffs(40, 400).addBox(-1.4532F, -18.8848F, -1.5349F, 3.0F, 19.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.682F, -22.4857F, -29.0142F, 0.8877F, 0.3751F, 0.3069F));

		PartDefinition WingPlaneS2_r1 = Wing1.addOrReplaceChild("WingPlaneS2_r1", CubeListBuilder.create().texOffs(82, 383).addBox(-0.0008F, -25.5257F, -3.021F, 0.0F, 13.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(246, 417).addBox(-1.0F, -26.5257F, -4.021F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0468F, -18.134F, -0.0011F, -0.6545F, 0.0F, 0.0F));

		PartDefinition WingPlaneS1_r1 = Wing1.addOrReplaceChild("WingPlaneS1_r1", CubeListBuilder.create().texOffs(156, 374).addBox(0.0F, -13.7508F, 0.9662F, 0.0F, 19.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(274, 403).addBox(-1.0F, -13.7508F, -1.0338F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0468F, -18.134F, -0.0011F, -0.4363F, 0.0F, 0.0F));

		PartDefinition Wing2 = Wings.addOrReplaceChild("Wing2", CubeListBuilder.create().texOffs(40, 400).addBox(-1.4532F, -18.8848F, -1.4651F, 2.0F, 19.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.8219F, -20.2391F, -31.4208F, -0.4514F, -0.3751F, 0.3069F));

		PartDefinition WingPlaneS3_r1 = Wing2.addOrReplaceChild("WingPlaneS3_r1", CubeListBuilder.create().texOffs(86, 388).addBox(0.0F, -5.0F, -2.5F, 0.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4539F, -27.171F, -8.7974F, 2.4871F, 0.0F, 3.1416F));

		PartDefinition WingRidgeS3_r1 = Wing2.addOrReplaceChild("WingRidgeS3_r1", CubeListBuilder.create().texOffs(246, 417).addBox(-1.0F, -18.5258F, 1.021F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0468F, -18.134F, 0.0011F, 0.6545F, 0.0F, 0.0F));

		PartDefinition WingPlaneS2_r2 = Wing2.addOrReplaceChild("WingPlaneS2_r2", CubeListBuilder.create().texOffs(159, 384).addBox(0.0F, -6.5F, -2.5F, 0.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4532F, -19.6153F, -4.5142F, 2.7053F, 0.0F, 3.1416F));

		PartDefinition WingRidgeS2_r1 = Wing2.addOrReplaceChild("WingRidgeS2_r1", CubeListBuilder.create().texOffs(274, 403).addBox(-1.0F, -9.7508F, -0.9662F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0468F, -18.134F, 0.0011F, 0.4363F, 0.0F, 0.0F));

		PartDefinition Wing3 = Wings.addOrReplaceChild("Wing3", CubeListBuilder.create().texOffs(42, 401).addBox(-1.4532F, -1.1152F, -1.4651F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-23.3623F, -0.5093F, -31.4208F, 0.0F, -0.1309F, 1.6144F));

		PartDefinition WingPlaneS3_r2 = Wing3.addOrReplaceChild("WingPlaneS3_r2", CubeListBuilder.create().texOffs(86, 386).addBox(0.0F, -6.0F, -2.5F, 0.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4532F, 20.171F, -8.7974F, 2.4871F, 0.0F, 0.0F));

		PartDefinition WingRidgeS3_r2 = Wing3.addOrReplaceChild("WingRidgeS3_r2", CubeListBuilder.create().texOffs(248, 418).addBox(-1.0F, 8.5257F, 1.021F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0468F, 11.134F, 0.0011F, -0.6545F, 0.0F, 0.0F));

		PartDefinition WingPlaneS2_r3 = Wing3.addOrReplaceChild("WingPlaneS2_r3", CubeListBuilder.create().texOffs(157, 378).addBox(0.0F, -5.5F, -2.5F, 0.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4532F, 12.6153F, -4.5142F, -2.7053F, 0.0F, -3.1416F));

		PartDefinition WingRidgeS2_r2 = Wing3.addOrReplaceChild("WingRidgeS2_r2", CubeListBuilder.create().texOffs(276, 404).addBox(-1.0F, -0.2492F, -0.9662F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0468F, 11.134F, 0.0011F, -0.4363F, 0.0F, 0.0F));

		PartDefinition Wing4 = Wings.addOrReplaceChild("Wing4", CubeListBuilder.create().texOffs(40, 400).addBox(-1.4532F, -0.1152F, -1.5349F, 3.0F, 19.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.682F, 19.7069F, -29.0142F, -1.2877F, -0.1623F, -0.2599F));

		PartDefinition WingPlaneS2_r4 = Wing4.addOrReplaceChild("WingPlaneS2_r4", CubeListBuilder.create().texOffs(82, 383).addBox(0.0F, -6.5F, -5.0F, 0.0F, 13.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0468F, 32.0234F, 13.151F, -0.6545F, 0.0F, -3.1416F));

		PartDefinition WingRidgeS2_r3 = Wing4.addOrReplaceChild("WingRidgeS2_r3", CubeListBuilder.create().texOffs(246, 417).addBox(-1.0F, 12.5258F, -4.021F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0468F, 18.134F, -0.0011F, 0.6545F, 0.0F, 0.0F));

		PartDefinition WingPlaneS1_r2 = Wing4.addOrReplaceChild("WingPlaneS1_r2", CubeListBuilder.create().texOffs(156, 374).addBox(0.0F, -9.5F, -4.5F, 0.0F, 19.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0468F, 19.6764F, 6.7494F, -0.4363F, 0.0F, -3.1416F));

		PartDefinition WingRidgeS1_r1 = Wing4.addOrReplaceChild("WingRidgeS1_r1", CubeListBuilder.create().texOffs(274, 403).addBox(-1.0F, -0.2492F, -1.0338F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0468F, 18.134F, -0.0011F, 0.4363F, 0.0F, 0.0F));

		PartDefinition EyeMasses = carrionite.addOrReplaceChild("EyeMasses", CubeListBuilder.create(), PartPose.offset(0.818F, -7.6106F, 7.6795F));

		PartDefinition EyeMass1 = EyeMasses.addOrReplaceChild("EyeMass1", CubeListBuilder.create().texOffs(0, 313).addBox(-6.0F, -9.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.818F, -15.3894F, -33.6795F, 0.48F, 0.2618F, -0.1745F));

		PartDefinition Eye_r1 = EyeMass1.addOrReplaceChild("Eye_r1", CubeListBuilder.create().texOffs(368, 241).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8136F, -8.1425F, 1.08F, -0.5569F, -0.3834F, 0.3305F));

		PartDefinition Eye_r2 = EyeMass1.addOrReplaceChild("Eye_r2", CubeListBuilder.create().texOffs(384, 154).addBox(-3.0F, -4.0F, -4.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5838F, -2.1581F, 4.0618F, -0.2636F, -0.6174F, -0.2631F));

		PartDefinition Eye_r3 = EyeMass1.addOrReplaceChild("Eye_r3", CubeListBuilder.create().texOffs(376, 414).addBox(-3.0F, -2.0F, -2.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7307F, -1.9756F, -6.3222F, -0.4247F, 0.2651F, -0.231F));

		PartDefinition Eye_r4 = EyeMass1.addOrReplaceChild("Eye_r4", CubeListBuilder.create().texOffs(398, 399).addBox(-1.0F, -1.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -9.0F, 0.0F, 0.3491F, 0.0F, 0.6109F));

		PartDefinition EyeMass2 = EyeMasses.addOrReplaceChild("EyeMass2", CubeListBuilder.create().texOffs(174, 244).addBox(-8.0F, -8.0F, -6.0F, 20.0F, 20.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-20.0545F, -13.8227F, -6.2467F, 0.3054F, 0.0F, 0.6981F));

		PartDefinition EyeMass3 = EyeMasses.addOrReplaceChild("EyeMass3", CubeListBuilder.create(), PartPose.offsetAndRotation(10.5394F, -16.98F, 2.3205F, -0.1745F, 0.0F, 0.4363F));

		PartDefinition mass = EyeMass3.addOrReplaceChild("mass", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mass_r8 = mass.addOrReplaceChild("mass_r8", CubeListBuilder.create().texOffs(106, 51).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8539F, -24.7333F, -1.0339F, -1.5708F, -1.0472F, 0.3491F));

		PartDefinition mass_r9 = mass.addOrReplaceChild("mass_r9", CubeListBuilder.create().texOffs(67, 77).addBox(-7.0F, -7.5F, -3.5F, 10.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.3887F, -5.9921F, 1.3501F, -1.5708F, -0.5236F, -0.3491F));

		PartDefinition mass_r10 = mass.addOrReplaceChild("mass_r10", CubeListBuilder.create().texOffs(148, 98).addBox(-4.0F, -3.5F, -3.5F, 8.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.6113F, -2.9921F, -3.6499F, -0.2618F, 1.693F, 0.5236F));

		PartDefinition mass_r11 = mass.addOrReplaceChild("mass_r11", CubeListBuilder.create().texOffs(90, 90).addBox(-5.0F, -9.0F, -4.5F, 10.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5038F, -15.7389F, -0.2859F, 0.0F, 0.0F, -0.2618F));

		PartDefinition mass_r12 = mass.addOrReplaceChild("mass_r12", CubeListBuilder.create().texOffs(76, 0).addBox(-4.5F, -6.0F, -3.5F, 9.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0038F, -11.7389F, -1.2859F, 0.1309F, -0.6981F, 0.3927F));

		PartDefinition mass_r13 = mass.addOrReplaceChild("mass_r13", CubeListBuilder.create().texOffs(3, 84).addBox(-5.0F, -6.0F, -5.5F, 10.0F, 12.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.5F, -0.2182F, 0.2618F, 0.0F));

		PartDefinition EyeMassCombs = mass.addOrReplaceChild("EyeMassCombs", CubeListBuilder.create(), PartPose.offset(-8.0F, -21.0F, 8.0F));

		PartDefinition bundle1 = EyeMassCombs.addOrReplaceChild("bundle1", CubeListBuilder.create(), PartPose.offsetAndRotation(16.4216F, 15.8589F, -3.6699F, -1.5708F, 1.0472F, -0.3491F));

		PartDefinition comb1 = bundle1.addOrReplaceChild("comb1", CubeListBuilder.create().texOffs(31, 52).addBox(-0.5574F, 5.0F, -5.7157F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(31, 52).addBox(4.2429F, 5.01F, -5.1651F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.25F, 7.0F, -5.75F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.25F, 7.0F, -6.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -8.0F, 2.0F));

		PartDefinition base_r1 = comb1.addOrReplaceChild("base_r1", CubeListBuilder.create().texOffs(31, 52).addBox(-2.4496F, -1.75F, -0.057F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(31, 52).addBox(-2.4496F, -1.74F, -4.557F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r2 = comb1.addOrReplaceChild("base_r2", CubeListBuilder.create().texOffs(31, 52).addBox(-2.624F, -1.74F, -3.0662F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(31, 52).addBox(2.1763F, -1.75F, -3.6168F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, 0.7854F, 0.0F));

		PartDefinition comb2 = bundle1.addOrReplaceChild("comb2", CubeListBuilder.create().texOffs(0, 0).addBox(3.75F, 7.01F, -3.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.75F, 7.01F, -1.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.75F, 7.01F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -8.0F, 2.0F));

		PartDefinition base_r3 = comb2.addOrReplaceChild("base_r3", CubeListBuilder.create().texOffs(8, 45).addBox(-2.1811F, -0.74F, 0.2412F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 45).addBox(-1.6811F, -0.75F, 3.8162F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r4 = comb2.addOrReplaceChild("base_r4", CubeListBuilder.create().texOffs(8, 45).addBox(-1.8148F, -0.75F, 1.0116F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 45).addBox(2.0617F, -0.74F, 0.9449F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r5 = comb2.addOrReplaceChild("base_r5", CubeListBuilder.create().texOffs(8, 45).addBox(0.873F, -0.75F, -1.4802F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 45).addBox(0.8063F, -0.74F, 2.3962F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, -0.3927F, 0.0F));

		PartDefinition bundle2 = EyeMassCombs.addOrReplaceChild("bundle2", CubeListBuilder.create(), PartPose.offsetAndRotation(10.0527F, 1.5548F, -4.25F, -1.5708F, 0.0F, -0.4363F));

		PartDefinition comb3 = bundle2.addOrReplaceChild("comb3", CubeListBuilder.create().texOffs(29, 51).addBox(1.5978F, 7.0F, 0.9846F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.25F, 7.25F, 1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(2.5F, -8.0F, -2.0F));

		PartDefinition base_r6 = comb3.addOrReplaceChild("base_r6", CubeListBuilder.create().texOffs(29, 51).addBox(0.7753F, -0.01F, 1.9001F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, 3.75F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r7 = comb3.addOrReplaceChild("base_r7", CubeListBuilder.create().texOffs(29, 51).addBox(0.1895F, -0.01F, -4.3143F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(29, 51).addBox(-0.3611F, 0.0F, 0.4859F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, 3.75F, 0.0F, -0.3927F, 0.0F));

		PartDefinition comb4 = bundle2.addOrReplaceChild("comb4", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 6.01F, 3.875F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, 6.01F, 2.875F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, 6.01F, 1.875F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, 6.01F, 2.875F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.25F, -8.0F, -3.5F));

		PartDefinition base_r8 = comb4.addOrReplaceChild("base_r8", CubeListBuilder.create().texOffs(22, 63).addBox(-0.0978F, -1.99F, -2.7654F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(17, 34).addBox(-4.5978F, -2.0F, -2.7654F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, 3.75F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r9 = comb4.addOrReplaceChild("base_r9", CubeListBuilder.create().texOffs(17, 34).addBox(0.4859F, -2.0F, -0.3611F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(17, 34).addBox(-4.3144F, -1.99F, 0.1895F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, 3.75F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r10 = comb4.addOrReplaceChild("base_r10", CubeListBuilder.create().texOffs(17, 34).addBox(-3.2247F, -2.0F, -2.9001F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(17, 34).addBox(-3.7753F, -1.99F, 1.9001F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, 3.75F, 0.0F, 0.3927F, 0.0F));

		PartDefinition comb5 = bundle2.addOrReplaceChild("comb5", CubeListBuilder.create(), PartPose.offset(2.75F, -8.5F, -1.25F));

		PartDefinition filler_r1 = comb5.addOrReplaceChild("filler_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.76F, -0.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, -0.75F, -1.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -0.75F, -2.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -0.75F, -0.25F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -0.75F, -2.25F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.25F, 7.75F, 0.0F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r11 = comb5.addOrReplaceChild("base_r11", CubeListBuilder.create().texOffs(16, 63).addBox(0.0F, -0.49F, -0.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.671F, 6.5F, -3.4898F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r12 = comb5.addOrReplaceChild("base_r12", CubeListBuilder.create().texOffs(16, 63).addBox(-0.5F, -0.51F, -5.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.099F, 6.5F, 1.5653F, 0.0F, -0.7854F, 0.0F));

		PartDefinition base_r13 = comb5.addOrReplaceChild("base_r13", CubeListBuilder.create().texOffs(16, 63).addBox(-3.2247F, -1.0F, -2.9001F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, -1.25F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r14 = comb5.addOrReplaceChild("base_r14", CubeListBuilder.create().texOffs(16, 63).addBox(-1.0F, -0.51F, 0.25F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0891F, 6.5F, -2.6953F, 0.0F, 0.6981F, 0.0F));

		PartDefinition bundle3 = EyeMassCombs.addOrReplaceChild("bundle3", CubeListBuilder.create(), PartPose.offsetAndRotation(9.1464F, 1.9774F, -11.75F, 1.5708F, 0.0F, -0.4363F));

		PartDefinition comb7 = bundle3.addOrReplaceChild("comb7", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 6.01F, -4.875F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, 6.01F, -6.875F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, 6.01F, -2.875F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, 6.01F, -5.875F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.25F, -8.0F, 3.5F));

		PartDefinition base_r15 = comb7.addOrReplaceChild("base_r15", CubeListBuilder.create().texOffs(11, 60).addBox(-0.0978F, -1.99F, -1.2346F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(11, 60).addBox(-4.5978F, -2.0F, -1.2346F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, -3.75F, 0.0F, -0.7854F, 0.0F));

		PartDefinition base_r16 = comb7.addOrReplaceChild("base_r16", CubeListBuilder.create().texOffs(11, 60).addBox(0.4859F, -2.0F, -2.6389F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(11, 60).addBox(-4.3144F, -1.99F, -3.1895F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, -3.75F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r17 = comb7.addOrReplaceChild("base_r17", CubeListBuilder.create().texOffs(11, 60).addBox(-3.2247F, -2.0F, 1.9001F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(11, 60).addBox(-3.7753F, -1.99F, -2.9001F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, -3.75F, 0.0F, -0.3927F, 0.0F));

		PartDefinition comb8 = bundle3.addOrReplaceChild("comb8", CubeListBuilder.create(), PartPose.offset(2.75F, -8.5F, 1.25F));

		PartDefinition filler_r2 = comb8.addOrReplaceChild("filler_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.76F, -1.75F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, -0.75F, 0.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -0.75F, 0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -0.75F, -0.75F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, -0.75F, 0.25F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.25F, 7.75F, 0.0F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r18 = comb8.addOrReplaceChild("base_r18", CubeListBuilder.create().texOffs(33, 28).addBox(0.0F, -0.49F, -0.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.671F, 6.5F, 3.4898F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r19 = comb8.addOrReplaceChild("base_r19", CubeListBuilder.create().texOffs(33, 28).addBox(-0.5F, -0.51F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.099F, 6.5F, -1.5653F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r20 = comb8.addOrReplaceChild("base_r20", CubeListBuilder.create().texOffs(33, 28).addBox(-3.2247F, -1.0F, 1.9001F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, 1.25F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r21 = comb8.addOrReplaceChild("base_r21", CubeListBuilder.create().texOffs(33, 28).addBox(-1.0F, -0.51F, -3.25F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0891F, 6.5F, 2.6953F, 0.0F, -0.6981F, 0.0F));

		PartDefinition bundle4 = EyeMassCombs.addOrReplaceChild("bundle4", CubeListBuilder.create(), PartPose.offsetAndRotation(12.4322F, -7.4798F, -9.0359F, 0.0F, -1.0472F, 0.0873F));

		PartDefinition comb6 = bundle4.addOrReplaceChild("comb6", CubeListBuilder.create().texOffs(9, 60).addBox(-0.5574F, 5.0F, -5.7157F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(9, 60).addBox(4.2429F, 5.01F, -5.1651F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.25F, 7.0F, -5.75F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.25F, 7.0F, -6.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -8.0F, 2.0F));

		PartDefinition base_r22 = comb6.addOrReplaceChild("base_r22", CubeListBuilder.create().texOffs(9, 60).addBox(-2.4496F, -1.75F, -0.057F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 60).addBox(-2.4496F, -1.74F, -4.557F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r23 = comb6.addOrReplaceChild("base_r23", CubeListBuilder.create().texOffs(9, 60).addBox(-2.624F, -1.74F, -3.0662F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(9, 60).addBox(2.1763F, -1.75F, -3.6168F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, 0.7854F, 0.0F));

		PartDefinition comb9 = bundle4.addOrReplaceChild("comb9", CubeListBuilder.create().texOffs(0, 0).addBox(3.75F, 6.26F, -3.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.75F, 6.26F, -1.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.75F, 6.26F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -8.0F, 2.0F));

		PartDefinition base_r24 = comb9.addOrReplaceChild("base_r24", CubeListBuilder.create().texOffs(37, 30).addBox(-2.1811F, -0.74F, 0.2412F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(37, 30).addBox(-1.6811F, -0.75F, 3.8162F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r25 = comb9.addOrReplaceChild("base_r25", CubeListBuilder.create().texOffs(37, 30).addBox(-1.8148F, -0.75F, 1.0116F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(37, 30).addBox(2.0617F, -0.74F, 0.9449F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r26 = comb9.addOrReplaceChild("base_r26", CubeListBuilder.create().texOffs(37, 30).addBox(0.873F, -0.75F, -1.4802F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(37, 30).addBox(0.8063F, -0.74F, 2.3962F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4496F, 6.75F, -2.443F, 0.0F, -0.3927F, 0.0F));

		PartDefinition limbsAndEtra = EyeMass3.addOrReplaceChild("limbsAndEtra", CubeListBuilder.create().texOffs(398, 399).addBox(3.624F, -30.0896F, -0.9225F, 6.0F, 6.0F, 6.0F, new CubeDeformation(-1.0F))
		.texOffs(398, 399).addBox(4.624F, -28.0896F, -8.9225F, 6.0F, 6.0F, 6.0F, new CubeDeformation(-1.0F))
		.texOffs(398, 399).addBox(-7.0408F, -24.2417F, -5.6321F, 6.0F, 6.0F, 6.0F, new CubeDeformation(-1.0F))
		.texOffs(398, 399).addBox(-8.624F, -12.0896F, -6.9226F, 6.0F, 6.0F, 6.0F, new CubeDeformation(-1.0F))
		.texOffs(398, 399).addBox(-0.4829F, -14.9059F, 0.3629F, 6.0F, 6.0F, 6.0F, new CubeDeformation(-1.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Smalltendril1 = limbsAndEtra.addOrReplaceChild("Smalltendril1", CubeListBuilder.create().texOffs(336, 418).addBox(-1.5F, -6.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3387F, -4.1619F, -1.9505F, 0.3478F, 0.0298F, 0.5288F));

		PartDefinition Seg2tendril1 = Smalltendril1.addOrReplaceChild("Seg2tendril1", CubeListBuilder.create().texOffs(94, 406).addBox(-1.0F, -6.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.4363F, 0.0F, 0.3927F));

		PartDefinition Seg3tendril1 = Seg2tendril1.addOrReplaceChild("Seg3tendril1", CubeListBuilder.create().texOffs(40, 422).addBox(-1.0F, -6.25F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.75F, 0.0F, 0.4363F, 0.0F, -0.3491F));

		PartDefinition Seg4tendril1 = Seg3tendril1.addOrReplaceChild("Seg4tendril1", CubeListBuilder.create().texOffs(364, 335).addBox(-0.5F, -7.5F, -0.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -5.75F, 0.0F, 0.0436F, 0.0F, 0.4363F));

		PartDefinition Seg5tendril1 = Seg4tendril1.addOrReplaceChild("Seg5tendril1", CubeListBuilder.create().texOffs(52, 391).addBox(-0.5F, -6.5F, -0.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.75F, 0.0F, -0.4068F, -0.1624F, -0.3591F));

		PartDefinition Smalltendril2 = limbsAndEtra.addOrReplaceChild("Smalltendril2", CubeListBuilder.create().texOffs(270, 419).addBox(-1.5F, -4.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.976F, -8.4842F, 5.3936F, -0.5672F, 0.0F, -0.5236F));

		PartDefinition Seg2tendril2 = Smalltendril2.addOrReplaceChild("Seg2tendril2", CubeListBuilder.create().texOffs(422, 212).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.3054F, 0.0F, -0.3927F));

		PartDefinition Seg3tendril2 = Seg2tendril2.addOrReplaceChild("Seg3tendril2", CubeListBuilder.create().texOffs(422, 320).addBox(-1.0F, -4.25F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.75F, 0.0F, -0.3999F, 0.1796F, 0.3999F));

		PartDefinition Seg4tendril2 = Seg3tendril2.addOrReplaceChild("Seg4tendril2", CubeListBuilder.create().texOffs(336, 107).addBox(-0.5F, -5.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -3.75F, 0.0F, -0.3491F, 0.0F, 0.3054F));

		PartDefinition Seg5tendril2 = Seg4tendril2.addOrReplaceChild("Seg5tendril2", CubeListBuilder.create().texOffs(374, 205).addBox(-0.5F, -4.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.75F, 0.0F, 0.48F, 0.0F, -0.5672F));

		PartDefinition EyeMass4 = EyeMasses.addOrReplaceChild("EyeMass4", CubeListBuilder.create().texOffs(178, 248).addBox(-12.0F, -4.0F, -6.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.8876F, 22.9464F, -14.7874F, -0.0464F, 0.3487F, -0.714F));

		PartDefinition Combs = carrionite.addOrReplaceChild("Combs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition LargeComb1 = Combs.addOrReplaceChild("LargeComb1", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.5784F, -10.5737F, 39.9588F, -1.3963F, 0.0F, 0.0F));

		PartDefinition Lcomb10 = LargeComb1.addOrReplaceChild("Lcomb10", CubeListBuilder.create().texOffs(0, 18).addBox(-10.2875F, 1.1F, -9.6957F, 3.0F, 5.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(0, 18).addBox(9.7729F, 1.1076F, -6.2259F, 4.0F, 5.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.5395F, 5.5924F, 5.4755F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.977F, 5.1F, -8.1877F, 3.0F, 1.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.05F, -6.1F, 1.525F));

		PartDefinition filler_r3 = Lcomb10.addOrReplaceChild("filler_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-11.077F, -1.0F, -4.0877F, 15.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.1F, 6.1F, -6.1F, -0.0436F, 0.0F, 0.0F));

		PartDefinition base_r27 = Lcomb10.addOrReplaceChild("base_r27", CubeListBuilder.create().texOffs(0, 18).addBox(-7.099F, -4.0469F, 7.5816F, 14.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 18).addBox(-8.4803F, -4.0393F, -11.8622F, 15.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6303F, 5.1469F, -1.8628F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r28 = Lcomb10.addOrReplaceChild("base_r28", CubeListBuilder.create().texOffs(0, 18).addBox(-1.9508F, -4.0393F, -2.3379F, 3.0F, 5.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.2822F, 5.1469F, 1.1872F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r29 = Lcomb10.addOrReplaceChild("base_r29", CubeListBuilder.create().texOffs(0, 18).addBox(6.4282F, -4.0469F, -8.4765F, 4.0F, 5.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6303F, 5.1469F, -1.8628F, 0.0F, 0.7854F, 0.0F));

		PartDefinition Lcomb11 = LargeComb1.addOrReplaceChild("Lcomb11", CubeListBuilder.create().texOffs(0, 0).addBox(-2.552F, 5.6F, -2.912F, 13.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-0.552F, 5.3576F, 5.9248F, 11.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(5.448F, 5.1076F, 9.6748F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(10.448F, 5.3576F, -1.5752F, 5.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(9.948F, 5.1076F, 7.1748F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.525F, -6.1F, 9.9125F));

		PartDefinition base_r30 = Lcomb11.addOrReplaceChild("base_r30", CubeListBuilder.create().texOffs(13, 35).addBox(-8.1963F, -4.0469F, 1.5338F, 3.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(13, 35).addBox(8.8658F, -4.0393F, 5.1048F, 3.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6303F, 5.1469F, -1.8628F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r31 = Lcomb11.addOrReplaceChild("base_r31", CubeListBuilder.create().texOffs(13, 35).addBox(3.1032F, -4.0469F, -4.9412F, 9.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(13, 35).addBox(-0.1852F, -4.0393F, 9.4521F, 13.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6303F, 5.1469F, -1.8628F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r32 = Lcomb11.addOrReplaceChild("base_r32", CubeListBuilder.create().texOffs(13, 35).addBox(-6.6568F, -4.0469F, 13.5849F, 13.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6303F, 5.1469F, -1.8628F, 0.0F, 0.7854F, 0.0F));

		PartDefinition LargeComb2 = Combs.addOrReplaceChild("LargeComb2", CubeListBuilder.create(), PartPose.offsetAndRotation(22.0816F, 3.4791F, -13.7309F, 0.0873F, 0.0F, 2.0071F));

		PartDefinition Lcomb12 = LargeComb2.addOrReplaceChild("Lcomb12", CubeListBuilder.create().texOffs(0, 55).addBox(5.0922F, 24.0F, 3.4462F, 4.0F, 4.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.625F, 25.625F, 3.25F, 11.0F, 2.0F, 15.0F, new CubeDeformation(0.1F)), PartPose.offset(8.75F, -28.0F, -7.0F));

		PartDefinition base_r33 = Lcomb12.addOrReplaceChild("base_r33", CubeListBuilder.create().texOffs(0, 55).addBox(2.2136F, -0.535F, 6.6505F, 11.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r34 = Lcomb12.addOrReplaceChild("base_r34", CubeListBuilder.create().texOffs(0, 55).addBox(1.1633F, -0.535F, -15.1002F, 10.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 55).addBox(0.2362F, -0.5F, 1.7007F, 9.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, -0.3927F, 0.0F));

		PartDefinition Lcomb13 = LargeComb2.addOrReplaceChild("Lcomb13", CubeListBuilder.create(), PartPose.offset(7.875F, -28.0F, -12.25F));

		PartDefinition filler_r4 = Lcomb13.addOrReplaceChild("filler_r4", CubeListBuilder.create().texOffs(0, 0).addBox(5.75F, -2.5F, -3.5F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.25F, -2.5F, -4.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.25F, -2.5F, -8.5F, 12.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.75F, 27.535F, 16.5625F, 0.0F, 0.0F, 0.2618F));

		PartDefinition base_r35 = Lcomb13.addOrReplaceChild("base_r35", CubeListBuilder.create().texOffs(19, 31).addBox(-0.8422F, -2.465F, -9.6788F, 4.0F, 6.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(19, 31).addBox(-16.5922F, -2.5F, -9.6788F, 4.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r36 = Lcomb13.addOrReplaceChild("base_r36", CubeListBuilder.create().texOffs(19, 31).addBox(2.2007F, -2.5F, -1.2638F, 3.0F, 6.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(19, 31).addBox(-15.6002F, -2.465F, 0.6633F, 4.0F, 10.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r37 = Lcomb13.addOrReplaceChild("base_r37", CubeListBuilder.create().texOffs(19, 31).addBox(-11.7865F, -2.5F, -10.1505F, 11.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(19, 31).addBox(-13.7135F, -2.465F, 6.6505F, 11.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, 0.3927F, 0.0F));

		PartDefinition Lcomb14 = LargeComb2.addOrReplaceChild("Lcomb14", CubeListBuilder.create(), PartPose.offset(9.625F, -30.25F, -4.375F));

		PartDefinition filler_r5 = Lcomb14.addOrReplaceChild("filler_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -2.125F, -4.125F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.0F, -2.125F, -7.875F, 15.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.875F, 27.125F, 0.0F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r38 = Lcomb14.addOrReplaceChild("base_r38", CubeListBuilder.create().texOffs(6, 36).addBox(-0.5F, 0.285F, -1.75F, 18.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.8484F, 22.75F, -12.2144F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r39 = Lcomb14.addOrReplaceChild("base_r39", CubeListBuilder.create().texOffs(6, 36).addBox(-2.25F, 0.215F, -17.5F, 4.0F, 7.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.3465F, 22.75F, 5.4787F, 0.0F, -0.7854F, 0.0F));

		PartDefinition base_r40 = Lcomb14.addOrReplaceChild("base_r40", CubeListBuilder.create().texOffs(6, 36).addBox(-11.7865F, -1.5F, -10.1505F, 11.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, -4.375F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r41 = Lcomb14.addOrReplaceChild("base_r41", CubeListBuilder.create().texOffs(6, 36).addBox(-3.0F, 1.215F, 0.875F, 3.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-21.3117F, 22.75F, -9.4337F, 0.0F, 0.6981F, 0.0F));

		PartDefinition LargeComb3 = Combs.addOrReplaceChild("LargeComb3", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.8536F, 16.7929F, 25.3687F, -2.7489F, 0.0F, 0.0F));

		PartDefinition Lcomb15 = LargeComb3.addOrReplaceChild("Lcomb15", CubeListBuilder.create().texOffs(0, 0).addBox(-15.0F, 16.025F, -14.1875F, 9.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, 16.025F, -14.1875F, 3.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-13.0F, 16.025F, -17.1875F, 8.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.625F, -20.0F, 8.75F));

		PartDefinition base_r42 = Lcomb15.addOrReplaceChild("base_r42", CubeListBuilder.create().texOffs(14, 38).addBox(0.2556F, -4.475F, -3.0866F, 2.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(14, 38).addBox(-11.9944F, -4.5F, -3.0866F, 3.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 17.5F, -9.375F, 0.0F, -0.7854F, 0.0F));

		PartDefinition base_r43 = Lcomb15.addOrReplaceChild("base_r43", CubeListBuilder.create().texOffs(14, 38).addBox(1.7148F, -4.5F, -6.5973F, 2.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(14, 38).addBox(-11.2859F, -4.475F, -8.4738F, 3.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 17.5F, -9.375F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r44 = Lcomb15.addOrReplaceChild("base_r44", CubeListBuilder.create().texOffs(14, 38).addBox(-8.5617F, -4.5F, 4.7504F, 8.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(14, 38).addBox(-9.9383F, -4.475F, -7.2504F, 8.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 17.5F, -9.375F, 0.0F, -0.3927F, 0.0F));

		PartDefinition Lcomb16 = LargeComb3.addOrReplaceChild("Lcomb16", CubeListBuilder.create(), PartPose.offset(6.875F, -21.25F, 3.125F));

		PartDefinition filler_r6 = Lcomb16.addOrReplaceChild("filler_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-9.6036F, -1.375F, 1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.6036F, -1.375F, 1.0F, 12.6036F, 3.0F, 4.625F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-9.0F, -1.375F, -2.0F, 11.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.625F, 19.375F, 0.0F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r45 = Lcomb16.addOrReplaceChild("base_r45", CubeListBuilder.create().texOffs(16, 41).addBox(-0.5F, -0.225F, -1.25F, 13.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.1775F, 16.25F, 8.7246F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r46 = Lcomb16.addOrReplaceChild("base_r46", CubeListBuilder.create().texOffs(16, 41).addBox(-1.75F, -0.275F, 0.0F, 3.0F, 5.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.2475F, 16.25F, -3.9133F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r47 = Lcomb16.addOrReplaceChild("base_r47", CubeListBuilder.create().texOffs(16, 41).addBox(-8.5617F, -1.5F, 4.7504F, 8.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 17.5F, 3.125F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r48 = Lcomb16.addOrReplaceChild("base_r48", CubeListBuilder.create().texOffs(16, 41).addBox(-3.0F, -0.275F, -6.125F, 3.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.2227F, 16.25F, 6.7384F, 0.0F, -0.6981F, 0.0F));

		PartDefinition LargeComb4 = Combs.addOrReplaceChild("LargeComb4", CubeListBuilder.create(), PartPose.offsetAndRotation(-21.1373F, -2.3292F, 1.9641F, 0.0F, 0.0F, -1.4835F));

		PartDefinition Lcomb17 = LargeComb4.addOrReplaceChild("Lcomb17", CubeListBuilder.create().texOffs(11, 59).addBox(-1.8891F, 14.08F, -14.3465F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(11, 59).addBox(10.1596F, 14.1051F, -12.9644F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.04F, -20.08F, 5.02F));

		PartDefinition filler_r7 = Lcomb17.addOrReplaceChild("filler_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.75F, -5.5F, 10.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.6675F, 18.58F, -9.6825F, 0.0F, 0.0F, 0.0873F));

		PartDefinition base_r49 = Lcomb17.addOrReplaceChild("base_r49", CubeListBuilder.create().texOffs(11, 59).addBox(-6.1084F, -2.8625F, -0.143F, 10.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(11, 59).addBox(-6.1084F, -2.8374F, -11.438F, 10.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r50 = Lcomb17.addOrReplaceChild("base_r50", CubeListBuilder.create().texOffs(11, 59).addBox(-7.0762F, -2.8374F, -7.6961F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(11, 59).addBox(4.9725F, -2.8625F, -9.0781F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, 0.7854F, 0.0F));

		PartDefinition Lcomb18 = LargeComb4.addOrReplaceChild("Lcomb18", CubeListBuilder.create(), PartPose.offset(-10.04F, -20.08F, 5.02F));

		PartDefinition filler_r8 = Lcomb18.addOrReplaceChild("filler_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -0.5F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.4325F, 18.7226F, -1.53F, 0.0F, 0.0F, -0.2182F));

		PartDefinition base_r51 = Lcomb18.addOrReplaceChild("base_r51", CubeListBuilder.create().texOffs(13, 62).addBox(-5.4344F, 0.1626F, 0.6055F, 10.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(13, 62).addBox(-4.6894F, 0.1375F, 9.5787F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r52 = Lcomb18.addOrReplaceChild("base_r52", CubeListBuilder.create().texOffs(13, 62).addBox(-5.045F, 0.1375F, 2.5391F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(13, 62).addBox(4.6847F, 0.1626F, 2.3716F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r53 = Lcomb18.addOrReplaceChild("base_r53", CubeListBuilder.create().texOffs(13, 62).addBox(2.2113F, 0.1375F, -3.7153F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(13, 62).addBox(1.5538F, 0.1626F, 6.0144F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, -0.3927F, 0.0F));

		PartDefinition LargeComb5 = Combs.addOrReplaceChild("LargeComb5", CubeListBuilder.create(), PartPose.offsetAndRotation(9.0351F, -16.9827F, -31.8293F, 1.227F, -0.21F, -0.4456F));

		PartDefinition Lcomb19 = LargeComb5.addOrReplaceChild("Lcomb19", CubeListBuilder.create().texOffs(18, 49).addBox(5.0922F, 24.0F, 3.4462F, 4.0F, 4.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.625F, 25.625F, 3.25F, 11.0F, 2.0F, 15.0F, new CubeDeformation(0.1F)), PartPose.offset(8.75F, -28.0F, -7.0F));

		PartDefinition base_r54 = Lcomb19.addOrReplaceChild("base_r54", CubeListBuilder.create().texOffs(18, 49).addBox(2.2135F, -0.535F, 6.6505F, 11.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r55 = Lcomb19.addOrReplaceChild("base_r55", CubeListBuilder.create().texOffs(18, 49).addBox(1.1633F, -0.535F, -15.1002F, 10.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(18, 49).addBox(0.2362F, -0.5F, 1.7007F, 9.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, -0.3927F, 0.0F));

		PartDefinition Lcomb20 = LargeComb5.addOrReplaceChild("Lcomb20", CubeListBuilder.create().texOffs(0, 0).addBox(-20.0F, 24.035F, 8.0625F, 12.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-22.0F, 24.035F, 14.0625F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-8.0F, 24.035F, 13.0625F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(7.875F, -28.0F, -12.25F));

		PartDefinition base_r56 = Lcomb20.addOrReplaceChild("base_r56", CubeListBuilder.create().texOffs(3, 53).addBox(-0.8422F, -2.465F, -9.6788F, 4.0F, 6.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(3, 53).addBox(-16.5922F, -2.5F, -9.6788F, 4.0F, 6.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r57 = Lcomb20.addOrReplaceChild("base_r57", CubeListBuilder.create().texOffs(3, 53).addBox(2.2007F, -2.5F, -1.2638F, 3.0F, 6.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(3, 53).addBox(-15.6002F, -2.465F, 0.6633F, 4.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r58 = Lcomb20.addOrReplaceChild("base_r58", CubeListBuilder.create().texOffs(3, 53).addBox(-11.7865F, -2.5F, -10.1505F, 11.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(3, 53).addBox(-13.7135F, -2.465F, 6.6505F, 11.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, 13.125F, 0.0F, 0.3927F, 0.0F));

		PartDefinition Lcomb21 = LargeComb5.addOrReplaceChild("Lcomb21", CubeListBuilder.create(), PartPose.offset(9.625F, -30.25F, -4.375F));

		PartDefinition filler_r9 = Lcomb21.addOrReplaceChild("filler_r9", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -2.125F, -4.125F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.0F, -2.125F, -7.875F, 15.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.875F, 27.125F, 0.0F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r59 = Lcomb21.addOrReplaceChild("base_r59", CubeListBuilder.create().texOffs(10, 27).addBox(-0.5F, 0.285F, -1.75F, 18.0F, 15.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.8484F, 22.75F, -12.2144F, 0.0F, -0.3927F, 0.0F));

		PartDefinition base_r60 = Lcomb21.addOrReplaceChild("base_r60", CubeListBuilder.create().texOffs(10, 27).addBox(-2.25F, 0.215F, -17.5F, 4.0F, 7.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.3465F, 22.75F, 5.4787F, 0.0F, -0.7854F, 0.0F));

		PartDefinition base_r61 = Lcomb21.addOrReplaceChild("base_r61", CubeListBuilder.create().texOffs(10, 27).addBox(-12.7865F, -1.5F, -10.1505F, 12.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 24.5F, -4.375F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r62 = Lcomb21.addOrReplaceChild("base_r62", CubeListBuilder.create().texOffs(10, 27).addBox(-3.0F, 1.215F, 0.875F, 3.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-21.3117F, 22.75F, -9.4337F, 0.0F, 0.6981F, 0.0F));

		PartDefinition LargeComb6 = Combs.addOrReplaceChild("LargeComb6", CubeListBuilder.create(), PartPose.offsetAndRotation(-21.214F, 9.1849F, -28.9534F, 2.9283F, 0.4026F, 0.5764F));

		PartDefinition Lcomb22 = LargeComb6.addOrReplaceChild("Lcomb22", CubeListBuilder.create().texOffs(29, 45).addBox(-1.8891F, 14.08F, -14.3465F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(29, 45).addBox(10.1596F, 14.1051F, -12.9644F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(0.6675F, 17.08F, -15.1825F, 10.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.04F, -20.08F, 5.02F));

		PartDefinition base_r63 = Lcomb22.addOrReplaceChild("base_r63", CubeListBuilder.create().texOffs(29, 45).addBox(-6.1084F, -2.8625F, -0.143F, 10.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(29, 45).addBox(-6.1084F, -2.8374F, -11.438F, 10.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r64 = Lcomb22.addOrReplaceChild("base_r64", CubeListBuilder.create().texOffs(29, 45).addBox(-7.0762F, -2.8374F, -7.6961F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(29, 45).addBox(4.9725F, -2.8625F, -9.0781F, 3.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, 0.7854F, 0.0F));

		PartDefinition Lcomb23 = LargeComb6.addOrReplaceChild("Lcomb23", CubeListBuilder.create().texOffs(0, 0).addBox(8.4325F, 18.2226F, -5.53F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.04F, -20.08F, 5.02F));

		PartDefinition base_r65 = Lcomb23.addOrReplaceChild("base_r65", CubeListBuilder.create().texOffs(22, 32).addBox(-5.4344F, 0.1626F, 0.6055F, 10.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(22, 32).addBox(-4.6894F, 0.1375F, 9.5787F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, 0.7854F, 0.0F));

		PartDefinition base_r66 = Lcomb23.addOrReplaceChild("base_r66", CubeListBuilder.create().texOffs(22, 32).addBox(-5.045F, 0.1375F, 2.5391F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(22, 32).addBox(4.6847F, 0.1626F, 2.3717F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, 0.3927F, 0.0F));

		PartDefinition base_r67 = Lcomb23.addOrReplaceChild("base_r67", CubeListBuilder.create().texOffs(22, 32).addBox(2.2113F, 0.1375F, -3.7154F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(22, 32).addBox(1.5538F, 0.1626F, 6.0144F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.6584F, 16.9425F, -6.132F, 0.0F, -0.3927F, 0.0F));

		PartDefinition Tendrils = carrionite.addOrReplaceChild("Tendrils", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition LargeTendril1 = Tendrils.addOrReplaceChild("LargeTendril1", CubeListBuilder.create().texOffs(380, 94).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-25.0881F, 9.5423F, -24.4334F, 0.2182F, 0.0F, 0.3491F));

		PartDefinition Seg2LargeTendril1 = LargeTendril1.addOrReplaceChild("Seg2LargeTendril1", CubeListBuilder.create().texOffs(102, 383).addBox(-2.25F, -1.25F, -2.25F, 5.0F, 15.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.7418F, 0.0F, 0.3927F));

		PartDefinition Seg3LargeTendril1 = Seg2LargeTendril1.addOrReplaceChild("Seg3LargeTendril1", CubeListBuilder.create().texOffs(406, 205).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.5F, 0.0F, -0.2618F, 0.0F, -0.3491F));

		PartDefinition Seg4LargeTendril1 = Seg3LargeTendril1.addOrReplaceChild("Seg4LargeTendril1", CubeListBuilder.create().texOffs(302, 416).addBox(-1.25F, 0.75F, -1.25F, 2.0F, 15.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 11.5F, 0.0F, 0.8432F, -0.0322F, -0.3922F));

		PartDefinition Seg5LargeTendril1 = Seg4LargeTendril1.addOrReplaceChild("Seg5LargeTendril1", CubeListBuilder.create().texOffs(160, 416).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.5F, 0.0F, 0.4185F, -0.1274F, 0.2783F));

		PartDefinition LargeTendril2 = Tendrils.addOrReplaceChild("LargeTendril2", CubeListBuilder.create().texOffs(242, 334).addBox(-4.5F, -12.0F, -4.5F, 9.0F, 15.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8675F, -19.462F, -36.6749F, 0.6677F, 0.2217F, -0.3592F));

		PartDefinition Seg2LargeTendril2 = LargeTendril2.addOrReplaceChild("Seg2LargeTendril2", CubeListBuilder.create().texOffs(364, 312).addBox(-3.5F, -14.0F, -3.5F, 7.0F, 16.0F, 7.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.3825F, 0.2046F, 0.3255F));

		PartDefinition Seg3LargeTendril2 = Seg2LargeTendril2.addOrReplaceChild("Seg3LargeTendril2", CubeListBuilder.create().texOffs(282, 376).addBox(-3.0F, -12.75F, -3.0F, 6.0F, 15.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.25F, 0.0F, -0.7661F, -0.0009F, 0.2091F));

		PartDefinition Seg4LargeTendril2 = Seg3LargeTendril2.addOrReplaceChild("Seg4LargeTendril2", CubeListBuilder.create().texOffs(102, 403).addBox(-2.0F, -17.0F, -2.0F, 4.0F, 16.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -11.25F, 0.0F, -0.6985F, -0.2332F, -0.4055F));

		PartDefinition Seg5LargeTendril2 = Seg4LargeTendril2.addOrReplaceChild("Seg5LargeTendril2", CubeListBuilder.create().texOffs(68, 415).addBox(-1.5F, -13.5F, -1.5F, 3.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.25F, 0.0F, -0.2618F, 0.0F, -0.5672F));

		PartDefinition LargeTendril3 = Tendrils.addOrReplaceChild("LargeTendril3", CubeListBuilder.create().texOffs(68, 345).addBox(-2.6F, -9.4F, -2.4F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-21.6272F, -4.474F, 5.5125F, -0.5669F, -1.0818F, -0.7228F));

		PartDefinition Seg2LargeTendril3 = LargeTendril3.addOrReplaceChild("Seg2LargeTendril3", CubeListBuilder.create().texOffs(16, 412).addBox(-2.25F, -11.05F, -1.75F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -9.6F, 0.0F, -0.3054F, 0.0F, 0.3927F));

		PartDefinition Seg3LargeTendril3 = Seg2LargeTendril3.addOrReplaceChild("Seg3LargeTendril3", CubeListBuilder.create().texOffs(352, 291).addBox(-1.4F, -9.8F, -1.6F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.8F, 0.0F, -0.2789F, 0.1622F, 0.5254F));

		PartDefinition Seg4LargeTendril3 = Seg3LargeTendril3.addOrReplaceChild("Seg4LargeTendril3", CubeListBuilder.create().texOffs(182, 418).addBox(-1.05F, -12.65F, -0.95F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -9.2F, 0.0F, -0.0426F, 0.0094F, 0.6543F));

		PartDefinition Seg5LargeTendril3 = Seg4LargeTendril3.addOrReplaceChild("Seg5LargeTendril3", CubeListBuilder.create().texOffs(348, 418).addBox(-1.2F, -10.2F, -0.8F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.4F, 0.0F, 0.3747F, 0.2291F, -0.5236F));

		PartDefinition LargeTendril4 = Tendrils.addOrReplaceChild("LargeTendril4", CubeListBuilder.create().texOffs(308, 276).addBox(-5.6F, -14.4F, -5.4F, 11.0F, 18.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(25.4967F, -0.7445F, -10.0387F, -0.1112F, -0.133F, 1.8616F));

		PartDefinition Seg2LargeTendril4 = LargeTendril4.addOrReplaceChild("Seg2LargeTendril4", CubeListBuilder.create().texOffs(288, 322).addBox(-4.75F, -17.55F, -4.25F, 9.0F, 20.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -14.4F, 0.0F, -0.6981F, 0.0F, -0.3927F));

		PartDefinition Seg3LargeTendril4 = Seg2LargeTendril4.addOrReplaceChild("Seg3LargeTendril4", CubeListBuilder.create().texOffs(278, 351).addBox(-3.4F, -15.3F, -3.6F, 7.0F, 18.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.1F, 0.0F, -0.3431F, 0.2751F, 0.6502F));

		PartDefinition Seg4LargeTendril4 = Seg3LargeTendril4.addOrReplaceChild("Seg4LargeTendril4", CubeListBuilder.create().texOffs(378, 115).addBox(-2.55F, -21.15F, -2.45F, 5.0F, 20.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -13.5F, 0.0F, -0.6735F, -0.1313F, -0.0658F));

		PartDefinition Seg5LargeTendril4 = Seg4LargeTendril4.addOrReplaceChild("Seg5LargeTendril4", CubeListBuilder.create().texOffs(52, 399).addBox(-2.2F, -16.2F, -1.8F, 4.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.7F, 0.0F, 0.48F, 0.0F, -0.5672F));

		PartDefinition LargeTendril5 = Tendrils.addOrReplaceChild("LargeTendril5", CubeListBuilder.create().texOffs(184, 284).addBox(-6.0F, -16.0F, -6.0F, 12.0F, 20.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8224F, -11.8396F, 44.1203F, -1.6646F, -0.1423F, 0.9731F));

		PartDefinition Seg2LargeTendril5 = LargeTendril5.addOrReplaceChild("Seg2LargeTendril5", CubeListBuilder.create().texOffs(48, 313).addBox(-5.25F, -19.25F, -4.75F, 10.0F, 22.0F, 10.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -16.0F, 0.0F, 0.3054F, 0.0F, 0.3927F));

		PartDefinition Seg3LargeTendril5 = Seg2LargeTendril5.addOrReplaceChild("Seg3LargeTendril5", CubeListBuilder.create().texOffs(0, 337).addBox(-4.0F, -17.0F, -4.0F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 0.3999F, 0.1796F, -0.3999F));

		PartDefinition Seg4LargeTendril5 = Seg3LargeTendril5.addOrReplaceChild("Seg4LargeTendril5", CubeListBuilder.create().texOffs(306, 355).addBox(-3.25F, -23.25F, -2.75F, 6.0F, 22.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -15.0F, 0.0F, 0.3491F, 0.0F, -0.3054F));

		PartDefinition Seg5LargeTendril5 = Seg4LargeTendril5.addOrReplaceChild("Seg5LargeTendril5", CubeListBuilder.create().texOffs(0, 393).addBox(-2.0F, -18.0F, -2.0F, 4.0F, 20.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -23.0F, 0.0F, -0.48F, 0.0F, 0.5672F));

		PartDefinition LargeTendril6 = Tendrils.addOrReplaceChild("LargeTendril6", CubeListBuilder.create().texOffs(88, 313).addBox(-4.38F, -18.92F, -4.62F, 9.0F, 22.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9387F, 3.4244F, 41.0089F, -1.0783F, -0.109F, 1.2324F));

		PartDefinition Seg2LargeTendril6 = LargeTendril6.addOrReplaceChild("Seg2LargeTendril6", CubeListBuilder.create().texOffs(334, 65).addBox(-4.4F, -20.94F, -3.6F, 8.0F, 23.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -18.48F, 0.0F, -0.3054F, 0.0F, 0.3927F));

		PartDefinition Seg3LargeTendril6 = Seg2LargeTendril6.addOrReplaceChild("Seg3LargeTendril6", CubeListBuilder.create().texOffs(32, 363).addBox(-2.92F, -19.69F, -3.08F, 6.0F, 22.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.79F, 0.0F, -0.4363F, 0.0F, -0.3491F));

		PartDefinition Seg4LargeTendril6 = Seg3LargeTendril6.addOrReplaceChild("Seg4LargeTendril6", CubeListBuilder.create().texOffs(0, 365).addBox(-2.94F, -24.02F, -2.06F, 5.0F, 23.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -17.71F, 0.0F, -0.0436F, 0.0F, 0.4363F));

		PartDefinition Seg5LargeTendril6 = Seg4LargeTendril6.addOrReplaceChild("Seg5LargeTendril6", CubeListBuilder.create().texOffs(20, 365).addBox(-1.46F, -20.46F, -1.54F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -23.87F, 0.0F, 0.4068F, 0.1624F, -0.3591F));

		PartDefinition LargeTendril7 = Tendrils.addOrReplaceChild("LargeTendril7", CubeListBuilder.create().texOffs(122, 380).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-24.2786F, 1.0344F, -2.7087F, 0.1682F, -0.1396F, 1.0354F));

		PartDefinition Seg2LargeTendril7 = LargeTendril7.addOrReplaceChild("Seg2LargeTendril7", CubeListBuilder.create().texOffs(396, 351).addBox(-2.25F, -1.25F, -2.25F, 5.0F, 15.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.602F, 0.3705F, 0.1795F));

		PartDefinition Seg3LargeTendril7 = Seg2LargeTendril7.addOrReplaceChild("Seg3LargeTendril7", CubeListBuilder.create().texOffs(206, 406).addBox(-2.0F, -1.5F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.5F, 0.0F, -0.2618F, 0.0F, -0.3491F));

		PartDefinition Seg4LargeTendril7 = Seg3LargeTendril7.addOrReplaceChild("Seg4LargeTendril7", CubeListBuilder.create().texOffs(312, 416).addBox(-1.25F, 0.75F, -1.25F, 2.0F, 15.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 11.5F, 0.0F, 0.8432F, -0.0322F, -0.3922F));

		PartDefinition Seg5LargeTendril7 = Seg4LargeTendril7.addOrReplaceChild("Seg5LargeTendril7", CubeListBuilder.create().texOffs(254, 417).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.5F, 0.0F, 0.4185F, -0.1274F, 0.2783F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer,
							   int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		carrionite.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override public RenderType getRenderType() {return RenderType.entityTranslucent(getResourceLocation());}
	@Override public ResourceLocation getResourceLocation() {return RESOURCE_LOCATION;}
	@Override public ModelLayerLocation getLayerLocation() {return LAYER_LOCATION;}
}