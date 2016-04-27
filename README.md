# epon
epon 系统仿真程序，主要是带宽分配算法。


dynamic bandwidth allocation algorithm in EPON;

reference ---- google scholar "IPACT";

customer.java represents the ONUs;

server.java represents the OLT;

packet.java carry the info of a packet;

simulation.java simulated the activity between the two clients;


this code is the source code of a paper published on a chinese core journal "optical commmunication";

the paper proposed a sleep scheme in EPON, and the sleep_threshold & wake_threshold denote the parameter in the scheme.

however, if the two thresholds =-1; the sleep scheme fails. It can be viewed as the the simualtion of normal EPON system.

if Tmax in customer.java =1000000(large enough), this can be viewed as the simulation of the gated service of IPACT;

if Tmax=M, this is a limited service with the service limit threshold =M.

It provides two packet arrivals distribution : ie,. poisson distribution and preto distribution, and you need to substitute the corresponding codes in the source code.(easy job)
