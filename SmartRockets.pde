Population p;
boolean stopped = true;

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
    if (!stopped)
    {
      p.run(timeScale); 
    }
}

void keyPressed()
{
  switch (key) {
    case '=':
      timeScale++;
      break;
    case '-':
      timeScale = 1;
      break;
    case 's':
      stopped = !stopped;
  }
}
