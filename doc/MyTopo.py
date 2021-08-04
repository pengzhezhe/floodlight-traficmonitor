from mininet.topo import Topo

class MyTopo( Topo ):
    "Simple topology example."

    def build( self ):
        "Create custom topo."

        # Add hosts and switches
        host1 = self.addHost('Trainee',ip= '10.0.0.1')
        host2 = self.addHost('Staff',ip= '10.0.0.2')
        host3 = self.addHost('Boss',ip= '10.0.0.3')
        host4 = self.addHost('Server',ip= '10.0.0.4')
        host5 = self.addHost('WAN',ip='10.0.0.5')
        host6 = self.addHost('Visitor',ip= '10.0.0.6')
        
        switch1 = self.addSwitch('s1')
        switch2 = self.addSwitch('s2')

        # Add links
        self.addLink(host1,switch1)
        self.addLink(host2,switch1)
        self.addLink(host3,switch1)
        self.addLink(switch1,switch2)
        self.addLink(host4,switch2)
        self.addLink(host5,switch2)
        self.addLink(host6,switch2) 


topos = { 'mytopo': ( lambda: MyTopo() ) }
