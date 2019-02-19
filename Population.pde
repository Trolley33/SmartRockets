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

    void evaluate ()
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

    void selection ()
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

    void run (int timeScale)
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
