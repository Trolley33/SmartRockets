import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SmartRockets extends PApplet {

Population p;

int timeScale;

public void setup ()
{
    
    p = new Population(300);
    timeScale = 1;
}

public void draw ()
{
    background(51);
    p.run(timeScale);
}

public void keyPressed()
{
    if (timeScale > 1)
    {
        timeScale = 1;
    }
    else
    {
        timeScale = 3;
    }
}
class DNA
{
    PVector[] genes;

    int lifeSpan;
    float maxForce = 0.5f;

    DNA (int lifeSpan_)
    {
        this.lifeSpan = lifeSpan_;
        this.genes = new PVector[lifeSpan];
        for (int i = 0; i < lifeSpan; i++)
        {
            this.genes[i] = PVector.random2D();
            this.genes[i].setMag(maxForce);
        }
    }

    DNA (int lifeSpan_, PVector[] genes_)
    {
        this.lifeSpan = lifeSpan_;
        this.genes = genes_;
    }

    public DNA crossOver (DNA other)
    {
        PVector[] newGenes = new PVector[genes.length];

        int mid = floor (random(genes.length));

        for (int i = 0; i < genes.length; i++)
        {
            if (i > mid)
            {
                newGenes[i] = genes[i];
            }
            else
            {
                newGenes[i] = other.genes[i];
            }
        }

        return new DNA(lifeSpan, newGenes);
    }

    public void mutation ()
    {
        for (int i = 0; i < genes.length; i++)
        {
            if (random(1) < 0.01f)
            {
                genes[i] = new PVector(genes[i].x + random(-1, 1), genes[i].y + random(-1, 1));
                genes[i].setMag(maxForce);
            }
        }
    }
}
class Obstacle
{
    PVector topLeft;
    PVector bottomRight;
    Obstacle (float x, float y, float w, float h)
    {
        this.topLeft = new PVector(x, y);
        this.bottomRight = new PVector(x + w, y + h);
    }

    public void show()
    {
        rectMode(CORNER);
        fill(255);
        rect(topLeft.x, topLeft.y, PVector.sub(bottomRight, topLeft).x, PVector.sub(bottomRight, topLeft).y);
    }

    public boolean isInside (PVector other)
    {
        if (other.x > topLeft.x && other.y > topLeft.y
            && other.x < bottomRight.x && other.y < bottomRight.y)
            {
                return true;
            }
        return false;
    }
}
class Population
{
    Rocket[] rockets;
    int popSize;
    int age;
    int lifeSpan;

    Obstacle box;


    PVector target;

    ArrayList<Rocket> matingPool;

    Population (int popSize_)
    {
        this.popSize = popSize_;

        age = 0;
        lifeSpan = 400;

        rockets = new Rocket[popSize];
        target = new PVector(width/2, 50);
        box = new Obstacle(150, 300, 300, 50);
        for (int i = 0; i < popSize; i++)
        {
            rockets[i] = new Rocket(width/2, height, lifeSpan, target);
        }

    }

    public void evaluate ()
    {
        float maxFit = 1;
        for (Rocket r : rockets)
        {
            r.calcFitness();
            if (r.fitness > maxFit)
            {
                maxFit = r.fitness;
            }
        }

        for (Rocket r : rockets)
        {
            r.fitness = r.fitness / maxFit;
        }

        matingPool = new ArrayList<Rocket> ();
        for (Rocket r : rockets)
        {
            float n = r.fitness * 100;
            for (int j = 0; j < n; j++)
            {
                this.matingPool.add(r);
            }
        }
    }

    public void selection ()
    {
        Rocket[] newRockets = new Rocket[rockets.length];
        for (int k = 0; k < rockets.length; k++)
        {
            int i = floor(random(0, matingPool.size()));
            int j = floor(random(0, matingPool.size()));
            DNA mother = matingPool.get(i).dna;
            DNA father = matingPool.get(j).dna;

            DNA child =  father.crossOver(mother);
            child.mutation();

            newRockets[k] = new Rocket(width/2, height, lifeSpan, target, child);
        }

        rockets = newRockets;


    }

    public void run (int timeScale)
    {

        for (int x = 0; x < timeScale; x++)
        {

            for (int i = 0; i < rockets.length; i++)
            {
                rockets[i].update(age, box);
                rockets[i].show();
            }

            age++;

            if (age >= lifeSpan)
            {
                age = 0;
                evaluate ();
                selection();
                break;
            }
        }


        box.show();

        fill(0, 0, 100, 100);
        ellipse(target.x,target.y,30,30);
    }
}
class Rocket
{

    PVector pos;
    PVector vel;
    PVector acc;

    PVector gravity = new PVector(0, 0);

    DNA dna;

    int c;

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

    public void applyForce (PVector force)
    {
        acc.add(force);
    }

    public void update (int age, Obstacle ob)
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

    public void show ()
    {
        pushMatrix();

        noStroke();
        fill(c);

        translate(pos.x,pos.y);
        rotate(vel.heading());
        rectMode(CENTER);
        rect(0, 0, 10, 2.5f);

        popMatrix();
    }

    public void calcFitness ()
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
    public void settings() {  size (600, 600); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "SmartRockets" };
        if (passedArgs != null) {
          PApplet.main(concat(appletArgs, passedArgs));
        } else {
          PApplet.main(appletArgs);
        }
    }
}
