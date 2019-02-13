class DNA
{
    PVector[] genes;

    int lifeSpan;
    float maxForce = 0.5;

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

    DNA crossOver (DNA other)
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

    void mutation ()
    {
        for (int i = 0; i < genes.length; i++)
        {
            if (random(1) < 0.01)
            {
                genes[i] = new PVector(genes[i].x + random(-1, 1), genes[i].y + random(-1, 1));
                genes[i].setMag(maxForce);
            }
        }
    }
}
