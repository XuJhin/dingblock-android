package cool.dingstock.appbase.widget.leonids.initializers;


import java.util.Random;

import cool.dingstock.appbase.widget.leonids.Particle;

public interface ParticleInitializer {

	void initParticle(Particle p, Random r);

}
