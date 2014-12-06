function RET= runSingleNodeModule(R4)
Pi = [0,0,0,0,0,0,0,0,0,0];
O=1;P=2;R=3;F=4;
r1=2;r2=0.8;r3=3;r4=R4;lmbd=1/(12*30*24);cv=10;cs=9;bta=0.08;
Q = { 0, r2+r2,     0,     0,        0,        0,        0,    0,    0,    0;
      0,     0,    r4,  lmbd, r2 + bta,        0,        0,    0,    0,    0;
     r3,     0,     0,     0,        0, r2 + bta,        0,    0,    0,    0;
     r1,     0,     0,     0,       r4,     lmbd, r2 + bta,    0,    0,    0;
      0,     0,     0,     0,        0,       r4,     lmbd,    0,    0,    0;
      0,     0,     0,     0,        0,        0,        0, lmbd,   r4,    0;
      0,     0,     0,     0,        0,        0,        0,   r4,    0, lmbd;
      0,     0,    r1,    r3,        0,        0,        0,    0,    0,    0;
      0,     0, r3+r3,     0,        0,        0,        0,    0,    0,    0;
      0,     0,     0, r1+r1,        0,        0,        0,    0,    0,    0;
      };
size=10;
M = zeros(size,size);
for j=1:size
for i=1:size
M(j,j)=M(j,j)+Q(j,i);
M(j,i)=M(j,i)-Q(i,j);
end
end
for i=1:size
    M(1,i)=1;
end
//A={M(1:3,1:4);M(5:5,1:4)}
A=M(1:4,1:4);
b={1;0;0;0};
x = Gauss(A,b)
Pi=x'
Cv=(1-Pi(O))*cv
Cs=(Pi(O)+Pi(P))*cs*r4
Ct=Cv+Cs
printf('%f\t%f\t%f\t%f\t%f\t%f\t%f\t%f\n',r4,Pi(O),Pi(P),Pi(R),Pi(F),Cv,Cs,Ct);
RET={r4;Pi(O);Cv;Cs;Ct}
endfunction
