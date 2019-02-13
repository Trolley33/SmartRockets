class Obstacle
{
    PVector topLeft;
    PVector bottomRight;
    Obstacle (float x, float y, float w, float h)
    {
        this.topLeft = new PVector(x, y);
        this.bottomRight = new PVector(x + w, y + h);
    }

    void show()
    {
        rectMode(CORNER);
        fill(255);
        rect(topLeft.x, topLeft.y, PVector.sub(bottomRight, topLeft).x, PVector.sub(bottomRight, topLeft).y);
    }

    boolean isInside (PVector other)
    {
        if (other.x > topLeft.x && other.y > topLeft.y
            && other.x < bottomRight.x && other.y < bottomRight.y)
            {
                return true;
            }
        return false;
    }
}
