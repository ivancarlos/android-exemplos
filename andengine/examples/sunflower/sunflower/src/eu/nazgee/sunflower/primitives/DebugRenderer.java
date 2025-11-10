package eu.nazgee.sunflower.primitives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.andengine.entity.Entity;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;


public class DebugRenderer extends Entity {

	private PhysicsWorld mWorld;
	private final VertexBufferObjectManager mVBO;
	private HashMap<Body, RenderBody> mToBeRenderred = new HashMap<Body, DebugRenderer.RenderBody>();

	public DebugRenderer(PhysicsWorld world, VertexBufferObjectManager pVBO) {
		super();
		this.mWorld = world;
		this.mVBO = pVBO;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		Iterator<Body> iterator = mWorld.getBodies();
		while (iterator.hasNext()) {
			Body body = iterator.next();
			RenderBody renderBody;
			if (!mToBeRenderred.containsKey(body)) {
				renderBody = new RenderBody(body, mVBO);
				mToBeRenderred.put(body, renderBody);
				this.attachChild(renderBody);
			} else {
				renderBody = mToBeRenderred.get(body);
				renderBody.setAlive();
			}

			renderBody.updateColor();
			renderBody.setRotationCenter(body.getMassData().center.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, body.getMassData().center.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
			renderBody.setRotation((float) (360 - body.getAngle() * (180 / Math.PI)));
			renderBody.setPosition(body.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, body.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
		}

		for (Iterator<RenderBody> renderBodyIter = mToBeRenderred.values().iterator(); renderBodyIter.hasNext();) {
			RenderBody renderBody = renderBodyIter.next();
			if (renderBody.isAlive()) {
				renderBody.setDead();
			} else {
				mToBeRenderred.remove(renderBody.body);
				this.detachChild(renderBody);
			}
		}
	}

	private static Color fixtureToColor(Fixture fixture) {
		if (fixture.isSensor()) {
			return Color.PINK;
		} else {
			Body body = fixture.getBody();
			if (!body.isActive()) {
				return Color.BLACK;
			} else {
				if (!body.isAwake()) {
					return Color.RED;
				} else {
					switch (body.getType()) {
					case StaticBody:
						return Color.CYAN;
					case KinematicBody:
						return Color.WHITE;
					case DynamicBody:
					default:
						return Color.GREEN;
					}
				}
			}
		}
	}

	private interface IRenderFixture {
		public Fixture getFixture();
		public Entity getEntity();
	}

	private abstract class RenderFixture implements IRenderFixture {
		protected final Fixture fixture;

		public RenderFixture(Fixture fixture) {
			super();
			this.fixture = fixture;
		}

		@Override
		public Fixture getFixture() {
			return fixture;
		}
	}

	private class RenderFixtureCircle extends RenderFixture {
		private Entity entity;

		public RenderFixtureCircle(Fixture fixture, VertexBufferObjectManager pVBO) {
			super(fixture);

			CircleShape fixtureShape = (CircleShape) fixture.getShape();
			Vector2 position = fixtureShape.getPosition();
			float radius = fixtureShape.getRadius() * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

			entity = new Ellipse(position.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, position.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, radius, radius, pVBO);
		}

		@Override
		public Entity getEntity() {
			return entity;
		}
	}

	private class RenderFixturePoly extends RenderFixture {
		private Entity entity;

		public RenderFixturePoly(Fixture fixture, VertexBufferObjectManager pVBO) {
			super(fixture);

			PolygonShape fixtureShape = (PolygonShape) fixture.getShape();
			int vSize = fixtureShape.getVertexCount();
			float[] xPoints = new float[vSize];
			float[] yPoints = new float[vSize];
			Vector2 vertex = new Vector2();

			for (int i = 0; i < fixtureShape.getVertexCount(); i++) {
				fixtureShape.getVertex(i, vertex);
				xPoints[i] = vertex.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
				yPoints[i] = vertex.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
			}

			entity = new PolyLine(0, 0, xPoints, yPoints, pVBO);
		}

		@Override
		public Entity getEntity() {
			return entity;
		}
	}

	private class RenderBody extends Entity {
		public Body body;
		public LinkedList<IRenderFixture> mRenderFixtures = new LinkedList<DebugRenderer.IRenderFixture>();
		private boolean mIsLive = true;

		public RenderBody(Body pBody, VertexBufferObjectManager pVBO) {
			this.body = pBody;
			ArrayList<Fixture> fixtures = pBody.getFixtureList();
			for (Fixture fixture : fixtures) {
				IRenderFixture renderfix;
				if (fixture.getShape().getType() == Type.Circle) {
					renderfix = new RenderFixtureCircle(fixture, pVBO);
				} else {
					renderfix = new RenderFixturePoly(fixture, pVBO);
				}

				updateColor();
				mRenderFixtures.add(renderfix);
				this.attachChild(renderfix.getEntity());
			}
		}

		public void updateColor() {
			for (IRenderFixture renderfix : mRenderFixtures) {
				renderfix.getEntity().setColor(fixtureToColor(renderfix.getFixture()));
			}
		}

		public boolean isAlive() {
			return mIsLive;
		}

		public void setAlive() {
			this.mIsLive = true;
		}

		public void setDead() {
			this.mIsLive = false;
		}
	}
}
