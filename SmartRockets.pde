Population p;

int timeScale;

void setup ()
{
    size (600, 600);
    p = new Population(300);
    timeScale = 1;
}

void draw ()
{
    background(51);
    p.run(timeScale);
}

void keyPressed()
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
