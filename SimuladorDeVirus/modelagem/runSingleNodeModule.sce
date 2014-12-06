function RET= runSingleNodeModule(R4)
Pi = [0,0,0,0];
O=1;P=2;R=3;F=4;
r1=2;r2=0.8;r3=3;r4=R4;lmbd=1/(12*30*24);cv=10;cs=9;
Q = { 0, r2,  0,    0;
      0,  0, r4, lmbd;
     r3,  0,  0,    0;
     r1,  0,  0,    0};
M = {1,1,1,1;
    r2,0,-r3,-r1;
    r2,-r4-lmbd,0,0;
    0,r4,-r3,0;
    0,lmbd,0,-r1};
//A={M(1:3,1:4);M(5:5,1:4)}
A=M(1:4,1:4);
b={1;0;0;0};
x = Gauss(A,b)
Pi=x'
Cv=(1-Pi(O))*cv
Cs=(Pi(O)+Pi(P))*cs*r4
Ct=Cv+Cs
printf('%f;%f;%f;%f;%f\n',r4,Pi(O),Cv,Cs,Ct);
RET={r4;Pi(O);Cv;Cs;Ct}
endfunction
