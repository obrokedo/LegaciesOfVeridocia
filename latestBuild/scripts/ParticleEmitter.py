from mb.jython import JParticleEmitter
from org.newdawn.slick import Image
import random
from java.lang import Math

class RandomHorizontalParticleEmitter(JParticleEmitter):
    drawX = 0
    width = 160
    interval = 150
    timer = 0
    
    def initialize(self, isHero):
        # Resolution is interpreted as 320x240
        if isHero:
            self.drawX = 160
        else:
            self.drawX = 0
        self.timer = 0
    
    def update(self, particleSystem, delta):
        self.timer = self.timer - delta
        if self.timer <= 0:
            self.timer = self.interval
            p = particleSystem.getNewParticle(self, 200);

            rand = random.randint(50, self.width)
            p.setSize(20)
            p.setLife(400)
            p.setPosition(rand + self.drawX, 125)


class RainParticleEmitter(JParticleEmitter):
    drawX = 0
    width = 160
    interval = 150
    timer = 0
    
    def initialize(self, isHero):
        # Resolution is interpreted as 320x240
        if isHero:
            self.drawX = 160
        else:
            self.drawX = 0
        self.timer = 0
    
    def update(self, particleSystem, delta):
        self.timer = self.timer - delta
        if self.timer <= 0:
            self.timer = self.interval
            p = particleSystem.getNewParticle(self, 200);

            p.setPosition(random.randint(0, self.width) + self.drawX, 0)
            p.setSize(20)
            p.setLife(400)
            p.setVelocity(-.01,.1, 3 + random.random() * 2)
            
class OrientedFlashParticleEmitter(JParticleEmitter):
    drawX = 0
    width = 120
    interval = 200
    timer = 0
    myImage = 0
    
    def initialize(self, isHero):
        # Resolution is interpreted as 320x240
        if isHero:
            self.drawX = 200
        else:
            self.drawX = 0
        self.timer = 0
        # self.myImage = image
    
    def update(self, particleSystem, delta):
        self.timer = self.timer - delta
        if self.timer <= 0:
            self.timer = self.interval
            p = particleSystem.getNewParticle(self, 1000);

            rand = random.randint(0, self.width)
            
            
            if rand < self.width / 2:
                p.flipHorizontal(False)
            else:
                p.flipHorizontal(True)
            

            p.adjustSize(160)
            p.setPosition(rand + self.drawX, 70)
            p.setLife(100)
            
class OrbitParticleEmitter(JParticleEmitter):
    drawX = 0
    width = 160
    interval = 300
    timer = 0
    
    def initialize(self, isHero):
        # Resolution is interpreted as 320x240
        if isHero:
            self.drawX = 160
        else:
            self.drawX = 0
        self.timer = 0
            
    def update(self, particleSystem, delta):
        self.timer = self.timer - delta
        if self.timer <= 0:
            self.timer = self.interval
            p = particleSystem.getNewParticle(self, 1000);

            p.setSize(20)
            p.setLife(4000)
            
    def updateParticle(self, particle, delta):
        life = 4000 - particle.getLife()
        if life < 1000:
            particle.setPosition(180 - life / 10, 100 * Math.sin(life / 1000) + 50)
        elif life < 1500:
            x = (1000 - life) / 500
            particle.setPosition((x * 40) + 80, 134.147 - Math.pow(x, 2) * 40) 
        elif life <  2500:
            x = (life - 2000) / 500
            particle.setPosition((x * 40) + 80, 54.147 + Math.pow(x, 2) * 40)
        else:
            x = (3000 - life) / 500
            particle.setPosition((x * 70) + 50, 154.147 - Math.pow(x, 2) * 60) 
