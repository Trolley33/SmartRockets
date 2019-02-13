class Rocket
{

    PVector pos;
    PVector vel;
    PVector acc;

    PVector gravity = new PVector(0, 0);

    DNA dna;

    color c;

    int age;
    float fitness;
    boolean complete = false;
    boolean crashed = false;
    PVector target;


    Rocket (float x, float y, int lifeSpan, PVector target_)
    {
        colorMode(HSB, 360, 100, 100);
        pos = new PVector(x, y);
        vel = new PVector(0, 0);
        acc = new PVector(0, 0);

        dna = new DNA (lifeSpan);
        float sum  = 0;
        for (PVector p : dna.genes)
        {
            sum += p.x * p.y * 50;
        }

        sum = sum % 300;

        c = color(sum, 100, 100);

        age = 0;

        target = target_;
    }

    Rocket (float x, float y, int lifeSpan, PVector target_, DNA dna_)
    {
        colorMode(HSB, 360, 100, 100);
        pos = new PVector(x, y);
        vel = new PVector(0, 0);
        acc = new PVector(0, 0);

        dna = dna_;

        float sum  = 0;
        for (PVector p : dna.genes)
        {
            sum += p.x * p.y * 50;
        }

        sum = sum % 300;
        c = color(sum, 100, 100);

        age = 0;

        target = target_;
    }

    void applyForce (PVector force)
    {
        acc.add(force);
    }

    void update (int age, Obstacle ob)
    {
        float d = dist(pos.x, pos.y, target.x, target.y);
        if (d < 30)
        {
            complete = true;
            pos = target.copy();
        }

        if (pos.x < 0 || pos.x > width || pos.y < -10 || pos.y > height || ob.isInside(pos))
        {
            crashed = true;
        }

        this.applyForce(this.dna.genes[age]);
        this.applyForce(gravity);
        if (!complete && !crashed)
        {
            vel.add(acc);
            pos.add(vel);
            acc.mult(0);
        }
    }

    void show ()
    {
        pushMatrix();

        noStroke();
        fill(c);

        translate(pos.x,pos.y);
        rotate(vel.heading());
        rectMode(CENTER);
        rect(0, 0, 10, 2.5);

        popMatrix();
    }

    void calcFitness ()
    {
        float d = dist(pos.x, pos.y, target.x, target.y);

        this.fitness =  map(d, 0, width, width, 0);
        if (complete)
        {
            this.fitness *= 100;
        }

        if (crashed)
        {
            this.fitness /= 2;
        }
    }
}
